package rosa.scanvas.demo.website.client.presenter;

import java.lang.NumberFormatException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.event.AnnotationSelectionEvent;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEventHandler;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent.PanelAction;
import rosa.scanvas.demo.website.client.event.PanelRequestEventHandler;
import rosa.scanvas.demo.website.client.widgets.AnnotationListWidget;
import rosa.scanvas.demo.website.client.widgets.ManifestListWidget;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.user.client.Window;

public class SidebarPresenter implements IsWidget {
	public interface Display extends IsWidget {
		HasClickHandlers getAddPanelButton();

		HasClickHandlers getRemovePanelButton();

		HasEnabled getRemovePanelEnabler();

		ListBox getPanelList();

		AnnotationListWidget getAnnoListWidget();

		ManifestListWidget getMetaListWidget();
	}

	private final Display display;
	private final HandlerManager eventBus;

	private HashMap<Integer, PanelData> dataMap = new HashMap<Integer, PanelData>();
	int currentIndex = 0;
	
	public SidebarPresenter(Display display, HandlerManager eventBus) {
		this.display = display;
		this.eventBus = eventBus;

		bind();
		display.getRemovePanelEnabler().setEnabled(false);

	}

	private void bind() {
		// event handlers listening in on the event bus
		eventBus.addHandler(PanelDisplayedEvent.TYPE, 
				new PanelDisplayedEventHandler() {
			public void onPanelDisplayed(PanelDisplayedEvent event) {
				doPanelDisplayed(event.getPanelId(),
						event.getPanelData());
			}
		});

		eventBus.addHandler(PanelRequestEvent.TYPE,
				new PanelRequestEventHandler() {
			public void onPanelRequest(PanelRequestEvent event) {
				doPanelRequest(event.getAction(), event.getPanelId());
			}
		});

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

		display.getPanelList().addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				doPanelListChange();
			}
		});
	}

	/**
	 * Removes item at specified index from the list. Panels in list are renamed
	 * 
	 * @param index
	 *            int of item to remove. Selects the first item in list.
	 */
	private void removePanelFromListBox(int index) {
		display.getPanelList().removeItem(index);

		for (int i = 0; i < display.getPanelList().getItemCount(); i++) {
			display.getPanelList().setItemText(i, "Panel " + (i + 1));
		}

		display.getPanelList().setSelectedIndex(0);
	}

	/**
	 * Adds a new item to the list and sets it to be selected.
	 * 
	 * @param item
	 */
	private void addPanelToListBox(String item) {
		display.getPanelList().addItem(item);
		
		display.getPanelList().setSelectedIndex(currentIndex);
	}

	private void doPanelDisplayed(int panelId, PanelData data) {

		if (data != null) {
			if (dataMap.containsKey(panelId)) {
				updateData(panelId, data);
				
			} else {
				// this will occur when a panel is added from a history token, so that
				// it will start from an arbitrary View, with associated data
				dataMap.put(panelId, data);
			}
			currentIndex = findIndexById(panelId);
			display.getPanelList().setSelectedIndex(currentIndex);
			setData(dataMap.get(panelId));
		} else {
			// this will happen when a new panel is added with the Add button
			// on the sidebar. It will start at the HomeView, so it sends
			// PanelData == null on display
			display.getPanelList().setValue(currentIndex, String.valueOf(panelId));
		}
	}

	private void doPanelRequest(PanelAction action, int panelId) {

		if (action == PanelAction.ADD) {
			if (!display.getRemovePanelEnabler().isEnabled() && 
					display.getPanelList().getItemCount() > 0) {
				display.getRemovePanelEnabler().setEnabled(true);
			}
			
			currentIndex = display.getPanelList().getItemCount();
			String item = "Panel " + (currentIndex + 1);
			
			addPanelToListBox(item);
			doPanelListChange();
		} else if (action == PanelAction.REMOVE) {
			display.getPanelList().setSelectedIndex(0);
			doPanelListChange();
			// if there is now only 1 item in the list, disable Remove button
			if (display.getPanelList().getItemCount() == 1) {
				display.getRemovePanelEnabler().setEnabled(false);
			}
		}
	}

	// -------------- DOM Event Actions --------------

	private void doAddPanel() {
		PanelRequestEvent event = new PanelRequestEvent(
				PanelRequestEvent.PanelAction.ADD, new PanelState());
		eventBus.fireEvent(event);
	}

	private void doRemovePanel() {
		int selectedPanel = display.getPanelList().getSelectedIndex();
		int id = Integer.parseInt(display.getPanelList().getValue(selectedPanel));
		
		removePanelFromListBox(selectedPanel);
		eventBus.fireEvent(new PanelRequestEvent(PanelAction.REMOVE, id));
	}

	private void doPanelListChange() {
		int selectedPanel = display.getPanelList().getSelectedIndex();
		/*eventBus.fireEvent(new PanelRequestEvent(PanelAction.CHANGE,
				selectedPanel));*/

		String itemValue = display.getPanelList().getValue(selectedPanel);
		PanelData dataToLoad = new PanelData();
		try {
			int id = Integer.parseInt(itemValue);
			
			if (dataMap.containsKey(id)) {
				dataToLoad = dataMap.get(id);
			}
		} catch (NumberFormatException e) {}
		
		setData(dataToLoad);
	}

	// -------------- End DOM Event Actions --------------

	private int findIndexById(int id) {
		String strId = String.valueOf(id);
		
		for (int i=0; i<display.getPanelList().getItemCount(); i++) {
			if (display.getPanelList().getValue(i).equals(strId)) {
				return i;
			}
		}
		
		return -1;
	}
	
	private void updateData(int id, PanelData data) {
		PanelData newData = dataMap.remove(id);

		if (data.getManifestCollection() != null) {
			newData.setManifestCollection(data.getManifestCollection());
		}

		if (data.getManifest() != null) {
			newData.setManifest(data.getManifest());
		}

		if (data.getSequence() != null) {
			newData.setSequence(data.getSequence());
		}

		if (data.getCanvas() != null) {
			newData.setCanvas(data.getCanvas());
		}
		
		if (data.getAnnotationLists() != null && 
				data.getAnnotationLists().size() != 
				newData.getAnnotationLists().size()) {
			newData.getAnnotationLists().clear();
			newData.getAnnotationLists().addAll(data.getAnnotationLists());
		}
		
		dataMap.put(id, newData);
	}

	/**
	 * Place data from selected panel in appropriate place in sidebar
	 */
	public void setData(PanelData data) {

		// Window.alert(String.valueOf(!this.data.getCollection().uri().equals(data.getCollection().uri())));

		/*
		 * if (this.data.getCollection() == null ||
		 * !this.data.getCollection().uri().equals(data.getCollection().uri()))
		 * { this.data.setCollection(data.getCollection()); } if
		 * (this.data.getManifest() == null ||
		 * !this.data.getManifest().uri().equals(data.getManifest().uri())) {
		 * this.data.setManifest(data.getManifest());
		 * this.data.getAnnotationLists().addAll(data.getAnnotationLists());
		 * this.data.getImageAnnotations().addAll(data.getImageAnnotations()); }
		 * if (this.data.getSequence() == null ||
		 * !this.data.getSequence().uri().equals(data.getSequence().uri())) {
		 * this.data.setSequence(data.getSequence()); } if
		 * (this.data.getCanvas() == null ||
		 * !this.data.getCanvas().uri().equals(data.getCanvas().uri())) {
		 * this.data.setCanvas(data.getCanvas()); }
		 * 
		 * setMetadata(); setAnnotations();
		 */
		display.getMetaListWidget().setMetadata(data);
		setAnnotations(data);
	}

	private void setAnnotations(PanelData data) {
		display.getAnnoListWidget().clearLists();
		
		List<AnnotationList> list = data.getAnnotationLists();
		if (list.size() > 0) {
			// iterate through the list of annotation lists
			Iterator<AnnotationList> listIterator = list.iterator();
			int i = 0, j = 0;
			while (listIterator.hasNext()) {
				// for each list, put each annotation in appropriate area
				AnnotationList annotationList = listIterator.next();
				Iterator<Annotation> annotationIterator = annotationList
						.iterator();
				while (annotationIterator.hasNext()) {
					Annotation annotation = annotationIterator.next();
					if (annotation.body().isImage()) {
						// TODO: ensure that image conformsTo() IIIF?
						// add to image annotation listbox
						display.getAnnoListWidget().getImageAnnoList()
							.setWidget(i, 1, new Label(
									annotation.label().replace(".", " ")));
						display.getAnnoListWidget().getImageAnnoList()
							.setWidget(i, 0, new CheckBox());
						bindImageRow(i, annotation);
						i++;
					} else if (annotation.body().isText()) {
						// add to text annotation listbox
						display.getAnnoListWidget()
							.getNontargetedTextAnnoList()
							.setWidget(j, 1, new Label(
									annotation.label().replace(".", " ")));
						display.getAnnoListWidget()
							.getNontargetedTextAnnoList()
							.setWidget(j, 0, new CheckBox());
						bindNontargetedTextRow(j, annotation);
						j++;
					}
				}
			}
		}
	}

	/**
	 * Add handlers to the image annotations list to listen to value changes of
	 * checkboxes
	 */
	private void bindImageRow(int row, final Annotation annotation) {
		((CheckBox) display.getAnnoListWidget().getImageAnnoList()
				.getWidget(row, 0))
				.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						// fire event on eventBus indicating an annotation
						// should be
						// shown or hidden
						boolean value = event.getValue();
						int panel = display.getPanelList().getSelectedIndex();

						// data.getVisibleAnnotations().add(annotation);
						eventBus.fireEvent(new AnnotationSelectionEvent(
								annotation, value, panel));
					}
				});
	}

	/**
	 * Add handlers to the nontargeted text annotations list to listen to value
	 * changes of checkboxes
	 */
	private void bindNontargetedTextRow(int row, final Annotation annotation) {
		((CheckBox) display.getAnnoListWidget().getNontargetedTextAnnoList()
				.getWidget(row, 0))
				.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						// fire event on eventBus indicating an annotation
						// should be
						// shown or hidden
						boolean value = event.getValue();
						int panel = display.getPanelList().getSelectedIndex();

						// data.getVisibleAnnotations().add(annotation);
						eventBus.fireEvent(new AnnotationSelectionEvent(
								annotation, value, panel));
					}
				});
	}

	@Override
	public Widget asWidget() {
		return display.asWidget();
	}
}
