package rosa.scanvas.demo.website.client;

import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.Canvas;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;
import rosa.scanvas.model.client.SharedCanvas;
import rosa.scanvas.demo.website.client.dynimg.MasterImage;
import rosa.scanvas.demo.website.client.event.AnnotationSelectionEvent;
import rosa.scanvas.demo.website.client.event.AnnotationSelectionHandler;
import rosa.scanvas.demo.website.client.event.DataUpdateEvent;
import rosa.scanvas.demo.website.client.event.DataUpdateEventHandler;
import rosa.scanvas.demo.website.client.event.GetDataEvent;
import rosa.scanvas.demo.website.client.event.GetDataEventHandler;
import rosa.scanvas.demo.website.client.event.PanelNumberChangeEvent;
import rosa.scanvas.demo.website.client.event.PanelNumberChangeEvent.PanelAction;
import rosa.scanvas.demo.website.client.event.PanelNumberChangeEventHandler;
import rosa.scanvas.demo.website.client.presenter.CanvasNavPresenter;
import rosa.scanvas.demo.website.client.presenter.CanvasPresenter;
import rosa.scanvas.demo.website.client.presenter.CollectionPresenter;
import rosa.scanvas.demo.website.client.presenter.HomePresenter;
import rosa.scanvas.demo.website.client.presenter.ManifestPresenter;
import rosa.scanvas.demo.website.client.presenter.Presenter;
import rosa.scanvas.demo.website.client.view.CanvasNavView;
import rosa.scanvas.demo.website.client.view.CanvasView;
import rosa.scanvas.demo.website.client.view.CollectionView;
import rosa.scanvas.demo.website.client.view.HomeView;
import rosa.scanvas.demo.website.client.view.ManifestView;
import rosa.scanvas.demo.website.client.widgets.DockPanel;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;

public class PanelController implements Controller {
	
	private final HandlerManager eventBus;
	private HasWidgets container;
	private int panelWidth;		// in pixels
	private int panelHeight;	// in pixels
	private ArrayList<PanelData> panelDataList = new ArrayList<PanelData>();
	
// --------------- Data loading callbacks ---------------
	
	private void getManifestCollection(final String token, final PanelProperties props, final Presenter presenter) {
		String url = HistoryInfo.getCollection(token, props.getIndex());
		
		if (panelDataList.get(props.getIndex()).getCollection() != null) {
			
			if (panelDataList.get(props.getIndex()).getCollection().uri().equals(url)) {
				if (presenter instanceof CollectionPresenter) {
					presenter.setData(panelDataList.get(props.getIndex()));
				} else {
					getManifest(token, props, presenter);
				}
			} else {
				// set collection to NULL
				panelDataList.get(props.getIndex()).clearCollection();
				getManifestCollection(token, props, presenter);
			}
		} else {
//			String url = HistoryInfo.getCollection(token, props.getIndex());
		
			SharedCanvas.load(url, ManifestCollection.class, new AsyncCallback<ManifestCollection>() {
				public void onFailure(Throwable caught) {
					// TODO error handling
				}
				// TODO handle error condition
				public void onSuccess(ManifestCollection result) {
					panelDataList.get(props.getIndex()).setCollection(result);
					if (presenter instanceof CollectionPresenter) {
						presenter.setData(panelDataList.get(props.getIndex()));
						eventBus.fireEvent(new DataUpdateEvent(panelDataList.get(props.getIndex())));
					} else {
						getManifest(token, props, presenter);
					}
					
				}
			});
		}
	}
	
	private void getManifest(final String token, final PanelProperties props, final Presenter presenter) {
		String url = HistoryInfo.getManifest(token, props.getIndex());
		final PanelData data = panelDataList.get(props.getIndex());
		
		if (/*panelDataList.get(props.getIndex()).getManifest() != null &&
				panelDataList.get(props.getIndex()).getAnnotationLists() != null*/
				data.getManifest() != null && data.getAnnotationLists() != null) {
			
			if (/*panelDataList.get(props.getIndex()).getManifest().uri().equals(url)*/
					data.getManifest().uri().equals(url)) {
				if (presenter instanceof ManifestPresenter) {
					presenter.setData(/*panelDataList.get(props.getIndex())*/data);
				} else {
					getSequence(token, props, presenter);
				}
			} else {
				//panelDataList.get(props.getIndex()).clearManifest();
				// set manifest to NULL
				data.clearManifest();
				getManifest(token, props, presenter);
			}
		} else {
//			String url = HistoryInfo.getManifest(token, props.getIndex());
		
			if (/*panelDataList.get(props.getIndex()).getAnnotationLists() != null 
					&& panelDataList.get(props.getIndex()).getAnnotationLists().size() > 0*/
					data.getAnnotationLists().size() > 0) {
				getSequence(token, props, presenter);
			} else {
				SharedCanvas.load(url, Manifest.class, new AsyncCallback<Manifest>() {
					public void onFailure(Throwable caught) {
						// TODO error handling
					}
					
					public void onSuccess(Manifest result) {
						//panelDataList.get(props.getIndex()).setManifest(result);
						data.setManifest(result);
						
						// get all annotation lists 
						//panelDataList.get(props.getIndex()).getAnnotationLists().clear();
						data.getAnnotationLists().clear();
						List<Reference<AnnotationList>> list = result.annotationsLists();
						Iterator<Reference<AnnotationList>> it = list.iterator();
						
						while (it.hasNext()) {
							String url = it.next().uri();
							SharedCanvas.load(url, AnnotationList.class, new AsyncCallback<AnnotationList>() {
								public void onFailure(Throwable caught) {
									// TODO error handling.....
								}
								
								public void onSuccess(AnnotationList result) {
									//panelDataList.get(props.getIndex()).getAnnotationLists().add(result);
									data.getAnnotationLists().add(result);
								}
							});
						}
						
						if (presenter instanceof ManifestPresenter) {
							presenter.setData(/*panelDataList.get(props.getIndex())*/data);
							eventBus.fireEvent(new DataUpdateEvent(/*panelDataList.get(props.getIndex())*/data));
						} else {
							getSequence(token, props, presenter);
						}
					}
				});
			}
		}
	}
	
	private void getSequence(final String token, final PanelProperties props, final Presenter presenter) {
		String url = HistoryInfo.getSequence(token, props.getIndex());
		
		if (panelDataList.get(props.getIndex()).getSequence() != null) {
			
			if (panelDataList.get(props.getIndex()).getSequence().uri().equals(url)) {
				if (presenter instanceof CanvasNavPresenter) {
					presenter.setData(panelDataList.get(props.getIndex()));
				} else {
					getCanvas(token, props, presenter);
				}
			} else {
				panelDataList.get(props.getIndex()).clearSequence();
				getSequence(token, props, presenter);
			}
			
		} else {
		
			SharedCanvas.load(url, Sequence.class, new AsyncCallback<Sequence>() {
				public void onFailure(Throwable caught) {
					// TODO error handling
				}
				
				public void onSuccess(Sequence result) {
					panelDataList.get(props.getIndex()).setSequence(result);
					getImageAnnotations(token, props, presenter);
					
					int tab = props.getTab();
					if (presenter instanceof CanvasNavPresenter/* && tab == 0*/) {
						presenter.setData(panelDataList.get(props.getIndex()));
						eventBus.fireEvent(new DataUpdateEvent(panelDataList.get(props.getIndex())));
					}/* else if (presenter instanceof CanvasNavPresenter && tab == 1) {
						presenter.setPageTurner(panelDataList.get(props.getIndex()));
						eventBus.fireEvent(new DataUpdateEvent(panelDataList.get(props.getIndex())));
					}*/ else {
						getCanvas(token, props, presenter);
					}
				}
			});
		}
	}
	
	
	private void getImageAnnotations(String token, final PanelProperties props, final Presenter presenter) {
		// separate out all the image annotations
		
		Iterator<AnnotationList> it = panelDataList.get(props.getIndex()).getAnnotationLists().iterator();
		while (it.hasNext()) {
			Iterator<Annotation> al = it.next().iterator();
			while (al.hasNext()) {
				Annotation anno = al.next();
				if (anno.body().isImage()) {
					panelDataList.get(props.getIndex()).getImageAnnotations().add(anno);
				}
			}
		}
	}
	
	
	private void getCanvas(String token, final PanelProperties props, final Presenter presenter) {
		try {
			int canvasIndex = Integer.parseInt(HistoryInfo.getCanvas(token, props.getIndex())); // throws NumberFormatException
			
			Canvas canvas = panelDataList.get(props.getIndex()).getSequence().canvas(canvasIndex);
			panelDataList.get(props.getIndex()).setCanvas(canvas);
			
			// TODO: find all annotations for this canvas
			// Use canvas.hasAnnotations() !
			
		} catch (NumberFormatException e) {
			Presenter pre = new HomePresenter(new HomeView(), eventBus, props);
			pre.go(container);
		}
	}
	
// --------------- end Data loading callbacks ---------------
	
	public PanelController(HandlerManager eventBus) {
		this.eventBus = eventBus;
		
		bind();
	}
	
	/**
	 * Bind event handlers to the eventBus
	 */
	private void bind() {
		History.addValueChangeHandler(this);
		
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				panelWidth = (int) (event.getWidth() * 0.75);
				panelHeight = (int) (event.getHeight() * 0.75);
				
				doResize();
			}
		});
		
		eventBus.addHandler(GetDataEvent.TYPE, new GetDataEventHandler() {
			public void retrieveData(GetDataEvent event) {
				doRetrieveData(event.getUrl());
			}
		});
		
		eventBus.addHandler(PanelNumberChangeEvent.TYPE, new PanelNumberChangeEventHandler() {
			public void onPanelNumberChange(PanelNumberChangeEvent event) {
				doPanelNumberChange(event.getMessage(), event.getSelectedPanel());
			}
		});
		
		eventBus.addHandler(AnnotationSelectionEvent.TYPE, new AnnotationSelectionHandler() {
			public void onSelection(AnnotationSelectionEvent event) {
				doAnnotationSelection(event.getAnnotation(), 
						event.getStatus(), event.getPanel());
			}
		});
		
	}
	
	public void go(HasWidgets container) {		
		this.container = container;
		/*panelWidth = (int) container.getCenterWidth();
		panelHeight= (int) container.getCenterHeight();*/
		
		if (History.getToken().equals("")) {
			eventBus.fireEvent(new PanelNumberChangeEvent(PanelNumberChangeEvent.PanelAction.ADD));
		} else {
			History.fireCurrentHistoryState();
		}
	}
	
	/**
	 * Listen for changes in History
	 */
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		
		if (token != null) {
			String[] panels = token.split(";:");
			
			// TODO: handle invalid history token
			
			container.clear();
			for (int i=0; i<panels.length; i++) {
				Presenter mainPresenter = null;
				PanelProperties props = new PanelProperties(i,
						HistoryInfo.getId(token, i), HistoryInfo.getView(token, i),
						Integer.parseInt(HistoryInfo.getTab(token, i)));
				
				// before getting data, find the index on Data stack
				// clean PanelData
				
				if (props.getView().equals("home")) {
					mainPresenter = new HomePresenter(new HomeView(), eventBus, props);
					((HomePresenter)mainPresenter).bindLinks();
				} else if (props.getView().equals("collection")) {
					mainPresenter = new CollectionPresenter(new CollectionView(), eventBus, props);
					getManifestCollection(token, props, mainPresenter);
				} else if (props.getView().equals("manifest")) {
					mainPresenter = new ManifestPresenter(new ManifestView(), eventBus, props);
					getManifestCollection(token, props, mainPresenter);
				} else if (props.getView().equals("canvasNav")) {
					mainPresenter = new CanvasNavPresenter(new CanvasNavView(), eventBus, props);
					setTab(panels[i], mainPresenter);
					getManifestCollection(token, props, mainPresenter);
				} else if (props.getView().equals("canvas")) {
					mainPresenter = new CanvasPresenter(new CanvasView(500, 500, 200, 200), eventBus, props);
					getCanvas(token, props, mainPresenter);
				}
				
				if (mainPresenter != null) {
					mainPresenter.setSize(String.valueOf(panelWidth), String.valueOf(panelHeight));
					mainPresenter.go(container);
				}
			}
		}
	}
	
	/**
	 * Occurs when a ManifestCollection, Manifest, or Sequence resource map is requested
	 * @param url in the standard form of a history token, contains the url of the resource map and the next view page
	 */
	private void doRetrieveData(String url) {
		String newToken = "";
		
		if (HistoryInfo.getView(url).equals("collection")) {
			newToken = HistoryInfo.setAttributeAndView(HistoryInfo.getId(url), 3, 
					HistoryInfo.getCollection(url), "collection");
		} else if (HistoryInfo.getView(url).equals("manifest")) {
			newToken = HistoryInfo.setAttributeAndView(HistoryInfo.getId(url), 4, 
					HistoryInfo.getManifest(url), "manifest");
		} else if (HistoryInfo.getView(url).equals("canvasNav")) {
			newToken = HistoryInfo.setAttributeAndView(HistoryInfo.getId(url), 5, 
					HistoryInfo.getSequence(url), "canvasNav");
		} else if (HistoryInfo.getView(url).equals("canvas")) {
			// TODO change the view to detailed canvas view
			// this will include the display of annotations
			newToken = HistoryInfo.setAttributeAndView(HistoryInfo.getId(url), 6,
					HistoryInfo.getCanvas(url), "canvas");
		}
		
		History.newItem(newToken);
	}
	
	private void doResize() {
		
	}
	
	/**
	 * Occurs when there is a PanelNumberChangeEvent. 
	 * On panel ADD, a new PanelData is added to the data list.
	 * On panel CHANGE, the correct data is sent to the sidebar
	 * On panel REMOVE, the correct PanelData is removed from the list
	 */
	private void doPanelNumberChange(PanelAction message, int selectedPanel) {
		if (message.equals(PanelAction.ADD)) {
			
			panelDataList.add(new PanelData());
			
		} else if (message.equals(PanelAction.CHANGE)) {
			
			eventBus.fireEvent(new DataUpdateEvent(panelDataList.get(selectedPanel)));
			
		} else if (message.equals(PanelAction.REMOVE)) {
			
			String[] parts = History.getToken().split(";:");
			String newToken = "";
			
			// remove appropriate PanelData from list
			// and remove the panel from History token
			try {
				
				panelDataList.remove(selectedPanel);
				
				for (int i=0; i<parts.length; i++) {
					if (i != selectedPanel) {
						newToken += parts[i] + ";:";
					}
				}
				History.newItem(newToken);
				
			} catch (IndexOutOfBoundsException e) {}
		}
	}
	
	private void doAnnotationSelection(Annotation annotation, boolean status, int panel) {
		if (status) {
			// checkbox checked, add to data
			if (!panelDataList.get(panel).getVisibleAnnotations().contains(annotation)) {
				panelDataList.get(panel).getVisibleAnnotations().add(annotation);
			}
			
		} else {
			// checkbox unchecked, remove from data
			panelDataList.get(panel).getVisibleAnnotations().remove(annotation);
		}
		
		
		//
		
		// update display
		//History.fireCurrentHistoryState();
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
