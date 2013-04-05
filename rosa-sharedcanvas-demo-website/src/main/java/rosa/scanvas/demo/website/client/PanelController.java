package rosa.scanvas.demo.website.client;

import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.SharedCanvas;
import rosa.scanvas.demo.website.client.event.GetDataEvent;
import rosa.scanvas.demo.website.client.event.GetDataEventHandler;
import rosa.scanvas.demo.website.client.event.PanelNumberChangeEvent;
import rosa.scanvas.demo.website.client.presenter.CanvasNavPresenter;
import rosa.scanvas.demo.website.client.presenter.CollectionPresenter;
import rosa.scanvas.demo.website.client.presenter.HomePresenter;
import rosa.scanvas.demo.website.client.presenter.ManifestPresenter;
import rosa.scanvas.demo.website.client.presenter.Presenter;
import rosa.scanvas.demo.website.client.view.CanvasNavView;
import rosa.scanvas.demo.website.client.view.CollectionView;
import rosa.scanvas.demo.website.client.view.HomeView;
import rosa.scanvas.demo.website.client.view.ManifestView;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class PanelController implements Controller {
	
	private final HandlerManager eventBus;
	private HasWidgets container;
	private PanelData data;
	
	
	private void getManifestCollection(String token, PanelProperties props, final Presenter presenter) {
		String url = HistoryInfo.getCollection(token, props.getIndex());
		
		SharedCanvas.load(url, ManifestCollection.class, new AsyncCallback<ManifestCollection>() {
			public void onFailure(Throwable caught) {
				
			}
			
			public void onSuccess(ManifestCollection result) {
				data.setCollection(result);
				presenter.setData(data);
			}
		});
	}
	
	
	public PanelController(HandlerManager eventBus) {
		this.eventBus = eventBus;
		
		bind();
	}
	
	private void bind() {
		History.addValueChangeHandler(this);
		
		eventBus.addHandler(GetDataEvent.TYPE, new GetDataEventHandler() {
			public void retrieveData(String url) {
				doRetrieveData(url);
			}
		});
		
	}
	
	public void go(HasWidgets container) {		
		this.container = container;
		
		if (History.getToken().equals("")) {
			eventBus.fireEvent(new PanelNumberChangeEvent("add"));
		} else {
			History.fireCurrentHistoryState();
		}
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();

		if (token != null) {
			String[] panels = token.split(";:");
			
			container.clear();
			for (int i=0; i<panels.length; i++) {
				Presenter mainPresenter = null;
				PanelProperties props = new PanelProperties(i,
						HistoryInfo.getId(token, i), HistoryInfo.getView(token, i));
				
				if (props.getView().equals("home")) {
					mainPresenter = new HomePresenter(new HomeView(), eventBus, props);
					((HomePresenter)mainPresenter).bindLinks();
				} else if (props.getView().equals("collection")) {
					//props.setData(collection);
					mainPresenter = new CollectionPresenter(new CollectionView(), eventBus, props);
					getManifestCollection(token, props, mainPresenter);
				} else if (props.getView().equals("manifest")) {
//					getManifestData(token, props);
					mainPresenter = new ManifestPresenter(new ManifestView(), eventBus, props);
				} else if (props.getView().equals("canvasNav")) {
					mainPresenter = new CanvasNavPresenter(new CanvasNavView(), eventBus, props);
					setTab(panels[i], mainPresenter);
				}
				
				if (mainPresenter != null) {
					mainPresenter.go(container);
				}
			}
		}
	}
	
/*	public void unhighlightPanel() {
		if (container instanceof FlexTable) {
			((FlexTable) container).getCellFormatter().removeStyleName(row, col, "selected");
		}
	}
	
	public void highlightPanel() {
		if (container instanceof FlexTable) {
			((FlexTable) container).getCellFormatter().addStyleName(row, col, "selected");
		}
	}*/
	
	private void doRetrieveData(String url) {
		String newView = "";
		
		if (HistoryInfo.getView(url).equals("collection")) {
			newView = "collection";
		} else if (HistoryInfo.getView(url).equals("manifest")) {
			newView = "manifest";
		} else if (HistoryInfo.getView(url).equals("canvasNav")) {
			newView = "canvasNav";
		}
		
		String newToken = HistoryInfo.changeView(HistoryInfo.getId(url), newView);
		History.newItem(newToken);
	}
	
	/**
	 * Set tab if attribute exists, otherwise do nothing
	 * @param token
	 * @param presenter
	 */
	private void setTab(String token, Presenter presenter) {
		int tab = 0;
		
		try {
			tab = Integer.parseInt(HistoryInfo.getTab(token));
			
			((CanvasNavPresenter)presenter).setSelectedTab(tab);
			
		} catch (IndexOutOfBoundsException e) {}
		catch (NumberFormatException e) {}
		catch (ClassCastException e) {}
	}


	public void setData(PanelData data) {
		// TODO Auto-generated method stub
		
	}
	
}
