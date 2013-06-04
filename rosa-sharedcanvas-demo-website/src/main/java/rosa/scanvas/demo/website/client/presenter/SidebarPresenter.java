package rosa.scanvas.demo.website.client.presenter;

import java.lang.NumberFormatException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.PanelView;
import rosa.scanvas.demo.website.client.disparea.AnnotationUtil;
import rosa.scanvas.demo.website.client.event.AnnotationSelectionEvent;
import rosa.scanvas.demo.website.client.event.AnnotationSelectionHandler;
import rosa.scanvas.demo.website.client.event.PanelAddedEvent;
import rosa.scanvas.demo.website.client.event.PanelAddedEventHandler;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEventHandler;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent.PanelAction;
import rosa.scanvas.demo.website.client.event.PanelRequestEventHandler;
import rosa.scanvas.demo.website.client.event.PanelSelectedEvent;
import rosa.scanvas.demo.website.client.event.PanelSelectedEventHandler;
import rosa.scanvas.demo.website.client.widgets.AnnotationListWidget;
import rosa.scanvas.demo.website.client.widgets.ManifestListWidget;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.AnnotationTarget;
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
		
		void addAnnoListTab();
		
		void removeAnnoListTab();
	}
	
	private final Display display;
	private final HandlerManager eventBus;

	private HashMap<Integer, PanelData> dataMap = new HashMap<Integer, PanelData>();
	int currentIndex = 0;
	private boolean default_image;
	
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

		eventBus.addHandler(PanelAddedEvent.TYPE,
				new PanelAddedEventHandler() {
			public void onPanelAdded(PanelAddedEvent event) {
				addPanelToListBox(event.getPanelId());
			}
		});
		
		eventBus.addHandler(PanelRequestEvent.TYPE,
				new PanelRequestEventHandler() {
			public void onPanelRequest(PanelRequestEvent event) {
				doPanelRequest(event.getAction(), event.getPanelId());
			}
		});
		
		eventBus.addHandler(AnnotationSelectionEvent.TYPE,
				new AnnotationSelectionHandler() {
			public void onSelection(AnnotationSelectionEvent event) {
				doAnnotationSelection(event.getPanel(), event.getAnnotation(),
						event.getStatus());
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
		
		display.getMetaListWidget().getSequencePickerBox().addChangeHandler(
				new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						doSequenceChange();
					}
				});
	}
	
	private void doSequenceChange() {
		ListBox picker = display.getMetaListWidget().getSequencePickerBox();
		String uri = picker.getValue(picker.getSelectedIndex());
		
		int panel_id = Integer.parseInt(display.getPanelList()
				.getValue(display.getPanelList().getSelectedIndex()));
		Manifest manifest = dataMap.get(panel_id).getManifest();
		
		PanelState state = new PanelState(PanelView.SEQUENCE, 
				uri, manifest.uri());
		PanelRequestEvent event = new PanelRequestEvent(
				PanelRequestEvent.PanelAction.CHANGE, panel_id, state);
		eventBus.fireEvent(event);
	}
	
	/**
	 * Removes item at specified index from the list. Panels in list are renamed
	 * 
	 * @param index
	 *            int of item to remove.
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
	 */
	private void addPanelToListBox(int panel_id) {
		if (!display.getRemovePanelEnabler().isEnabled() && 
				display.getPanelList().getItemCount() > 0) {
			display.getRemovePanelEnabler().setEnabled(true);
		}
		
		currentIndex = display.getPanelList().getItemCount();
		String item = "Panel " + (currentIndex + 1);
		
		display.getPanelList().addItem(item);
		display.getPanelList().setValue(currentIndex, String.valueOf(panel_id));
		display.getPanelList().setSelectedIndex(currentIndex);
		
		doPanelListChange();
	}

	/**
	 * Adds new data that is associated with a particular panel to the sidebar
	 * 
	 * @param panelId
	 * 			id number identifying the associated panel
	 * @param data
	 * 			new data
	 */
	private void doPanelDisplayed(int panelId, PanelData data) {

		if (data != null) {
			if (dataMap.containsKey(panelId)) {
				updateData(panelId, data);
				
			} else {
				// this will occur when a panel is added from a history token, so that
				// it will start from an arbitrary View, with associated data
				dataMap.put(panelId, data);
			}
		} else {
			// this will happen when a new panel is added with the Add button
			// on the sidebar. It will start at the HomeView, so it sends
			// PanelData == null on display
			dataMap.put(panelId, new PanelData());
		}
		setData(dataMap.get(panelId));
	}

	/**
	 * Performs appropriate action to the panel specified by its id, 
	 * based off the PanelAction specified
	 * 
	 * @param action
	 * @param panelId
	 */
	private void doPanelRequest(PanelAction action, int panelId) {
		// Panel add is ignored here
		if (action == PanelAction.REMOVE) {
			int index = findIndexById(panelId);
			removePanelFromListBox(index);
			
			display.getPanelList().setSelectedIndex(0);
			doPanelListChange();
			// if there is now only 1 item in the list, disable Remove button
			if (display.getPanelList().getItemCount() == 1) {
				display.getRemovePanelEnabler().setEnabled(false);
			}
		} else if (action == PanelAction.CHANGE) {
			currentIndex = findIndexById(panelId);
			display.getPanelList().setSelectedIndex(currentIndex);
			doPanelListChange();
		}
	}

	/**
	 * Sets the status of an annotation on a specified panel
	 * 
	 * @param panel_id
	 * @param ann
	 * @param status
	 * 			whether or not an annotation is selected
	 */
	private void doAnnotationSelection(int panel_id, Annotation ann,
			boolean status) {
		PanelData data = dataMap.get(panel_id);
		data.setAnnotationStatus(ann, status);
	}
	
	// -------------- DOM Event Actions --------------

	private void doAddPanel() {
		PanelRequestEvent event = new PanelRequestEvent(
				PanelRequestEvent.PanelAction.ADD, new PanelState());
		eventBus.fireEvent(event);
	}

	private void doRemovePanel() {
		int selectedPanel = display.getPanelList().getSelectedIndex();
		String idStr = display.getPanelList().getValue(selectedPanel);
		
		try {
			int id = Integer.parseInt(idStr);
			eventBus.fireEvent(new PanelRequestEvent(PanelAction.REMOVE, id));
		} catch(NumberFormatException e) {
			Window.alert("Incorrect ID format: " + idStr);
		}
	}

	private void doPanelListChange() {
		int selectedPanel = display.getPanelList().getSelectedIndex();

		String itemValue = display.getPanelList().getValue(selectedPanel);
		PanelData dataToLoad = null;
		try {
			int id = Integer.parseInt(itemValue);
			
			if (dataMap.containsKey(id)) {
				dataToLoad = dataMap.get(id);
			}
			eventBus.fireEvent(new PanelSelectedEvent(id));
		} catch (NumberFormatException e) { dataToLoad = new PanelData(); }
		
		if (dataToLoad != null) {
			setData(dataToLoad);
		}
	}

	// -------------- End DOM Event Actions --------------

	/**
	 * Find the index in the list based on its id
	 * 
	 * @param id
	 * 			number uniquely identifying a panel
	 */
	private int findIndexById(int id) {
		String strId = String.valueOf(id);
		
		for (int i=0; i<display.getPanelList().getItemCount(); i++) {
			if (display.getPanelList().getValue(i).equals(strId)) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Updates the displayed data associated with a particular panel
	 * 
	 * @param id
	 * 			number identifying the associated panel
	 * @param data
	 * 			incoming data
	 */
	private void updateData(int id, PanelData data) {
		PanelData newData = dataMap.get(id);

		if (data.getManifestCollection() != null) {
			newData.setManifestCollection(data.getManifestCollection());
		}

		if (data.getManifest() != null) {
			newData.setManifest(data.getManifest());
		} else {
			newData.setManifest(null);
			newData.setSequence(null);
			newData.setCanvas(null);
		}

		if (data.getSequence() != null) {
			newData.setSequence(data.getSequence());
		} else if (data.getSequence() == null && data.getManifest() != null) {
			newData.setSequence(null);
			newData.setCanvas(null);
			// this will clear the Annotation Lists
			newData.setManifest(newData.getManifest());
		}

		if (data.getCanvas() != null) {
			newData.setCanvas(data.getCanvas());
		}
		
		if (data.getAnnotationLists() != null) {
			newData.getAnnotationLists().clear();
			newData.getAnnotationLists().addAll(data.getAnnotationLists());
		}
		
		dataMap.put(id, newData);
	}

	/**
	 * Place data from selected panel in appropriate place in sidebar.
	 * The Annotation List is displayed only when appropriate.
	 */
	public void setData(PanelData data) {
		display.getMetaListWidget().setMetadata(data);
		setAnnotations(data);
		
		if (data.getCanvas() == null) {
			display.removeAnnoListTab();
		} else {
			display.addAnnoListTab();
		}
	}
	
// TODO: move this to AnnotationListWidget, plus messy
	/**
	 * Displays all annotations into the AnnotationListWidget
	 */
	private void setAnnotations(PanelData data) {
		default_image = false;
		display.getAnnoListWidget().clearLists();
		
		List<AnnotationList> list = data.getAnnotationLists();
		if (list.size() > 0) {
			// iterate through the list of annotation lists
			int i = 0, j = 0, k = 0;
			for (AnnotationList al : list) {
				// for each list, put each annotation in appropriate area
				for (Annotation anno : al) {
					CheckBox checkbox = new CheckBox();
					checkbox.setValue(data.getAnnotationStatus(anno), false);
				
					if (anno.body().isImage()) {
					
						// send the first image that targets the whole canvas to be displayed
						if (!AnnotationUtil.isSpecificResource(anno)
								&& !default_image) {
							// the boolean variable  'default_image' is used ensure that only
							// a single image is used in this way
							try {
								int panel_id = Integer.parseInt(display.getPanelList()
										.getValue(display.getPanelList()
												.getSelectedIndex()));
								eventBus.fireEvent(new AnnotationSelectionEvent(
										anno, true, panel_id));
								checkbox.setValue(true, false);
								
								default_image = true;
							} catch (NumberFormatException e) {}
						}
						
						display.getAnnoListWidget().getImageAnnoList()
							.setWidget(i, 0, checkbox);
						display.getAnnoListWidget().getImageAnnoList()
							.setWidget(i, 1, new Label(anno.label()));
						i++;
						
					} else if (anno.body().isText()) {
						// check if the text annotation is targeted
						if (AnnotationUtil.isSpecificResource(anno)) {
							display.getAnnoListWidget().getTargetedTextAnnoList()
									.setWidget(k, 0, checkbox);
							display.getAnnoListWidget().getTargetedTextAnnoList()
									.setWidget(k, 1, new Label(anno.label()));
							k++;
						} else {
							display.getAnnoListWidget().getNontargetedTextAnnoList()
									.setWidget(j, 0, checkbox);
							display.getAnnoListWidget().getNontargetedTextAnnoList()
									.setWidget(j, 1, new Label(anno.label()));
							j++;
						}
					}
					
					bindCheckBox(checkbox, anno);
				}
			}
		}
	}

	/**
	 * Binds an event handler to the checkboxes in the annotation lists
	 */
	private void bindCheckBox(CheckBox box, final Annotation annotation) {
		box.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				// fire event on eventBus indicating an annotation
				// should be shown or hidden
				boolean value = event.getValue();
				int index = display.getPanelList().getSelectedIndex();
				
				try {
					int panel = Integer.parseInt(display.getPanelList()
										.getValue(index));

					// data.getVisibleAnnotations().add(annotation);
					eventBus.fireEvent(new AnnotationSelectionEvent(
							annotation, value, panel));
				} catch (NumberFormatException e) {}
			}
		});
	}

	@Override
	public Widget asWidget() {
		return display.asWidget();
	}
}
