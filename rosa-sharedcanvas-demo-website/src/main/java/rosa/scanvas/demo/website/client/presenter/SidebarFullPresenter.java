package rosa.scanvas.demo.website.client.presenter;

import java.util.Iterator;
import java.util.List;

import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Sequence;
import rosa.scanvas.demo.website.client.HistoryInfo;
import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.event.AnnotationSelectionEvent;
import rosa.scanvas.demo.website.client.event.PanelNumberChangeEvent;
import rosa.scanvas.demo.website.client.event.PanelNumberChangeEvent.PanelAction;
import rosa.scanvas.demo.website.client.widgets.AnnotationListWidget;
import rosa.scanvas.demo.website.client.widgets.ManifestListWidget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class SidebarFullPresenter implements Presenter {

	public interface Display {
		HasClickHandlers getAddPanelButton();
		HasClickHandlers getRemovePanelButton();
		ListBox getPanelList();
		AnnotationListWidget getAnnoListWidget();
		ManifestListWidget getMetaListWidget();
		Widget asWidget();
	}
	
	private final Display display;
	private final HandlerManager eventBus;
	
	public SidebarFullPresenter(Display display, HandlerManager eventBus){
		this.display = display;
		this.eventBus = eventBus;
	}
	
	public void go(HasWidgets container) {
		bind();
		if (container instanceof DockLayoutPanel) {
			((DockLayoutPanel)container).addWest(display.asWidget(), 300);
		}
	}
	
	private void bind() {
		// event handlers for the Panel List list and buttons
		display.getAddPanelButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				doAddPanel();
			}
		});
		
		display.getRemovePanelButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				doRemovePanel();
			}
		});
		
		display.getPanelList().addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				doPanelListChange();
			}
		});
	}
	
	/**
	 * Refresh list according to current history token
	 * @param token
	 */
	public void refreshList(String token) {
		
		display.getPanelList().clear();
		for (int i=0; i<HistoryInfo.getNumItems(token); i++) {
			display.getPanelList().addItem("Panel "+ (i+1));
		}
		
	}
	
	/**
	 * Set the selected item in the Panel list
	 */
	public void setListSelected(int index) {
		display.getPanelList().setSelectedIndex(index);
	}
	
	/**
	 * is the Panel list empty?
	 */
	public boolean isListEmpty() {
		return (display.getPanelList().getItemCount() == 0);
	}
	
	/**
	 * Removes item at specified index from the list. Panels in list are renamed
	 * @param index int of item to remove. Selects the first item in list.
	 */
	public void removePanel(int index) {
		display.getPanelList().removeItem(index);
		
		for (int i=0; i<display.getPanelList().getItemCount(); i++) {
			display.getPanelList().setItemText(i, "Panel "+(i+1));
		}
		
		display.getPanelList().setSelectedIndex(0);
	}
	
	/**
	 * Adds a new item to the list and sets it to be selected.
	 * @param item
	 */
	public void addPanel(String item) {
		display.getPanelList().addItem(item);
		
		int selectedPanel = display.getPanelList().getItemCount()-1;
		display.getPanelList().setSelectedIndex(selectedPanel);
		
//		eventBus.fireEvent(new PanelNumberChangeEvent("change", selectedPanel));
	}
	
	private void doAddPanel() {		
		eventBus.fireEvent(new PanelNumberChangeEvent(PanelAction.ADD));
	}
	
	private void doRemovePanel() {
		eventBus.fireEvent(new PanelNumberChangeEvent(PanelAction.REMOVE));
	}
	
	private void doPanelListChange() {
		int selectedPanel = display.getPanelList().getSelectedIndex();
		eventBus.fireEvent(new PanelNumberChangeEvent(PanelAction.CHANGE, selectedPanel));
	}

	public void setData(PanelData data) {
		// TODO display a list of sequences for the 'sequence picker'
		display.getMetaListWidget().clearLabels();
		display.getAnnoListWidget().clearLists();
		
		ManifestCollection collection = data.getCollection();
		if (collection != null) {
			display.getMetaListWidget().newCollectionLabel(collection.label());
			display.getMetaListWidget().newCollectionLabel("Number of items: " + collection.manifests().size());
		}
		
		Manifest manifest = data.getManifest();
		if (manifest != null) {
			display.getMetaListWidget().newManifestLabel(manifest.label());
			display.getMetaListWidget().newManifestLabel("Agent: " + manifest.agent());
			display.getMetaListWidget().newManifestLabel("Location: " + manifest.location());
			display.getMetaListWidget().newManifestLabel("Date: " + manifest.date());
			display.getMetaListWidget().newManifestLabel("Description: " + manifest.description());
			display.getMetaListWidget().newManifestLabel("");
			display.getMetaListWidget().newManifestLabel("Rights: " + manifest.rights());
		}
		
		Sequence sequence = data.getSequence();
		if (sequence != null) {
			display.getMetaListWidget().newSequenceLabel(sequence.label());
			display.getMetaListWidget().newSequenceLabel(sequence.uri());
			display.getMetaListWidget().newSequenceLabel("Number of canvases: " + sequence.size());
		}
		
		List<AnnotationList> list = data.getAnnotationLists();
		if (list.size() > 0) {
			// iterate through the list of annotation lists
			Iterator<AnnotationList> listIterator = list.iterator();
			int i = 0, j = 0;
			while (listIterator.hasNext()) {
				// for each list, put each annotation in appropriate area
				AnnotationList annotationList = listIterator.next();
				Iterator<Annotation> annotationIterator = annotationList.iterator();
				while (annotationIterator.hasNext()) {
					Annotation annotation = annotationIterator.next();
					if (annotation.body().isImage()) {
						// ensure that image conformsTo() IIIF
						// add to image annotation listbox
						display.getAnnoListWidget().getImageAnnoList()
								.setWidget(i, 1, new Label(annotation.body().uri()));
						display.getAnnoListWidget().getImageAnnoList()
								.setWidget(i, 0, new CheckBox());
						bindImageRow(i, annotation);
						i++;
					} else if (annotation.body().isText()) {
						// add to text annotation listbox
						display.getAnnoListWidget().getNontargetedTextAnnoList()
								.setWidget(j, 1, new Label(annotation.body().uri()/* + 
												annotation.body().targets().get(0)*/));
						display.getAnnoListWidget().getNontargetedTextAnnoList()
								.setWidget(j, 0, new CheckBox());
						bindNontargetedTextRow(j, annotation);
						j++;
					}
				}
			}
		}
		
	}
	
	private void bindImageRow(int row, final Annotation annotation) {
		((CheckBox)display.getAnnoListWidget().getImageAnnoList().getWidget(row,0))
			.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					// fire event on eventBus indicating an annotation should be
					// shown or hidden
					boolean value = event.getValue();
					int panel = display.getPanelList().getSelectedIndex();
					
					eventBus.fireEvent(new AnnotationSelectionEvent(annotation, value, panel));
				}
		});
	}
	
	private void bindNontargetedTextRow(int row, final Annotation annotation) {
		((CheckBox)display.getAnnoListWidget().getNontargetedTextAnnoList().getWidget(row,0))
			.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					// fire event on eventBus indicating an annotation should be
					// shown or hidden
					boolean value = event.getValue();
					int panel = display.getPanelList().getSelectedIndex();
					
					eventBus.fireEvent(new AnnotationSelectionEvent(annotation, value, panel));
				}
		});
	}

}
