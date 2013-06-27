package rosa.scanvas.demo.website.client.presenter;

import java.util.ArrayList;
import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.PanelView;
import rosa.scanvas.demo.website.client.disparea.AnnotationUtil;
import rosa.scanvas.demo.website.client.disparea.TranscriptionViewer;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.demo.website.client.event.PanelMoveEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;
import rosa.scanvas.demo.website.client.widgets.AnnotationListWidget;
import rosa.scanvas.demo.website.client.widgets.ManifestListWidget;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.Canvas;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.user.client.Window;

public abstract class BasePanelPresenter implements PanelPresenter {
    public interface Display extends IsWidget {
    	ToggleButton getOptionsButton();
    	
    	ToggleButton getAnnotationsButton();
    	
    	ToggleButton getMetadataButton();
    	
    	ToggleButton getTextAnnotationsButton();
    	
    	PopupPanel getOptionsPopup();
    	
    	PopupPanel getAnnotationsPopup();
    	
    	PopupPanel getMetadataPopup();
    	
    	PopupPanel getTextAnnotationsPopup();
    	
    	HasClickHandlers getCloseButton();
    	
    	HasClickHandlers getCloseLabel();
    	
    	HasClickHandlers getDuplicateButton();
    	
    	HasClickHandlers getDuplicateLabel();
    	
    	HasClickHandlers getSwapHorizontalButton();
    	
    	HasClickHandlers getSwapHorizontalLabel();
    	
    	HasClickHandlers getMoveUpButton();
    	
    	HasClickHandlers getMoveDownButton();
    	
    	AnnotationListWidget getAnnoListWidget();
    	
    	ManifestListWidget getMetaListWidget();
    	
    	/**
         * Adds a label to the titlebar and returns it so event handlers can be attached.
         * 
         * @param text
         */
    	Label addContextLabel(String text);
    	
    	void clearContextLabels();

        void resize(int width, int height);
        
        void selected(boolean is_selected);
    }

    private final Display display;
    private final HandlerManager event_bus;
    private final int panel_id;
    
    private PanelData data;
    
    private boolean anno_list_ready;
	private boolean meta_list_ready;
	private boolean text_list_ready;
	private boolean default_image;
	
	//private TabLayoutPanel tab_panel;
    
    public BasePanelPresenter(Display display, HandlerManager event_bus,
            int panel_id) {
        this.display = display;
        this.event_bus = event_bus;
        this.panel_id = panel_id;

        bind();
    }

    private void bind() {
    	// When one button is clicked, untoggle all other buttons and hide
    	// all other menus
        display.getOptionsButton().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		if (display.getOptionsButton().isDown()) {
        			display.getOptionsPopup().setVisible(false);
        			display.getOptionsPopup().setPopupPosition(0, 0);
        			display.getOptionsPopup().show();
        		}
        	}
        });
        
        display.getAnnotationsButton().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		/*if (!anno_list_ready) {
        			set_annotations_list();
        		}*/
        		
        		if (display.getAnnotationsButton().isDown()) {
        			display.getAnnotationsPopup().setVisible(false);
        			display.getAnnotationsPopup().setPopupPosition(0, 0);
        			display.getAnnotationsPopup().show();
        		}
        	}
        });
        
        display.getMetadataButton().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		if (!meta_list_ready) {
        			set_metadata_list();
        		}
        		
        		if (display.getMetadataButton().isDown()) {
        			display.getMetadataPopup().setVisible(false);
        			display.getMetadataPopup().setPopupPosition(0, 0);
        			display.getMetadataPopup().show();
        		}
        	}
        });
        
        display.getTextAnnotationsButton().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		if (!text_list_ready) {
        			set_text_annotations_list();
        		}
        		
        		if (display.getTextAnnotationsButton().isDown()) {
        			display.getTextAnnotationsPopup().setVisible(false);
        			display.getTextAnnotationsPopup().setPopupPosition(0, 0);
        			display.getTextAnnotationsPopup().show();
        		}
        	}
        });
        
        display.getCloseButton().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		display.getOptionsPopup().hide();
        		
        		PanelRequestEvent req = new PanelRequestEvent(
        				PanelRequestEvent.PanelAction.REMOVE, panel_id);
        		event_bus.fireEvent(req);
        	}
        });
        
        display.getCloseLabel().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		display.getOptionsPopup().hide();
        		
        		PanelRequestEvent req = new PanelRequestEvent(
        				PanelRequestEvent.PanelAction.REMOVE, panel_id);
        		event_bus.fireEvent(req);
        	}
        });
        
        display.getDuplicateButton().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		PanelRequestEvent req = new PanelRequestEvent(
        				PanelRequestEvent.PanelAction.ADD, panel_id);
        		event_bus.fireEvent(req);
        	}
        });
        
        display.getDuplicateLabel().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		PanelRequestEvent req = new PanelRequestEvent(
        				PanelRequestEvent.PanelAction.ADD, panel_id);
        		event_bus.fireEvent(req);
        	}
        });
        
        display.getSwapHorizontalButton().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		PanelMoveEvent move = new PanelMoveEvent(
        				PanelMoveEvent.PanelDirection.HORIZONTAL, panel_id);
        		event_bus.fireEvent(move);
        	}
        });
        
        display.getSwapHorizontalLabel().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		PanelMoveEvent move = new PanelMoveEvent(
        				PanelMoveEvent.PanelDirection.HORIZONTAL, panel_id);
        		event_bus.fireEvent(move);
        	}
        });
        
        display.getMoveUpButton().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		PanelMoveEvent move = new PanelMoveEvent(
        				PanelMoveEvent.PanelDirection.UP, panel_id);
        		event_bus.fireEvent(move);
        	}
        });
        
        display.getMoveDownButton().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		PanelMoveEvent move = new PanelMoveEvent(
        				PanelMoveEvent.PanelDirection.DOWN, panel_id);
        		event_bus.fireEvent(move);
        	}
        });
    }

    public HandlerManager eventBus() {
    	return event_bus;
    }
    
    public int panelId() {
    	return panel_id;
    }
    
    public PanelData data() {
    	return data;
    }
    
    @Override
    public void display(PanelData data) {
    	this.data = data;
    	
    	display.getAnnotationsButton().setEnabled(true);
    	display.getMetadataButton().setEnabled(true);
    	display.getTextAnnotationsButton().setEnabled(true);
    	
    	anno_list_ready = false;
		meta_list_ready = false;
		text_list_ready = false;
		default_image = false;
    	
    	if (data == null) {
    		display.getAnnotationsButton().setEnabled(false);
    		display.getMetadataButton().setEnabled(false);
    		display.getTextAnnotationsButton().setEnabled(false);
    		return;
    	}
    	
    	if (data.getManifestCollection() == null 
    			&& data.getManifest() == null && data.getSequence() == null) {
    		display.getMetadataButton().setEnabled(false);
    	}
    	
    	if (data.getCanvas() == null) {
    		display.getAnnotationsButton().setEnabled(false);
    		display.getTextAnnotationsButton().setEnabled(false);
    	}
    	
    	set_context();
    	set_annotations_list();
    }
    
    /**
     * Setup the Metadata menu and add data
     */
    private void set_metadata_list() {
    	if (data == null) {
    		return;
    	}
    	
    	display.getMetaListWidget().setMetadata(data);
    	meta_list_ready = true;
    }
    
    /**
     * Setup the List of Annotations menu and add data
     */
    private void set_annotations_list() {
    	AnnotationListWidget anno_list = display.getAnnoListWidget();
    	anno_list.clearLists();
    	
    	if (data == null) {
    		return;
    	}
    	
		List<AnnotationList> list = data.getAnnotationLists();
		if (list == null || list.size() == 0) {
			return;
		}
		
		// iterate through the list of annotation lists
		int i = 0, k = 0;
		for (AnnotationList al : list) {
			// TODO: change the way default images are assigned based on annotation
			//	targets. Currently, there is 1 default image assigned per
			//	annotation list. While this may work for the Rose data, it will not
			//	work correctly in general, as a single canvas may have multiple
			//	annotation lists.
			default_image = false;
			// for each list, put each annotation in appropriate area
			for (final Annotation anno : al) {
				final CheckBox checkbox = new CheckBox();
				
				if (anno.body().isImage()) {
					
					if (!default_image && !AnnotationUtil.isSpecificResource(anno)) {
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {    
							@Override
							public void execute() {
								checkbox.setValue(true, true);
							}
						});
						
						default_image = true;
					} 
					
					anno_list.getImageAnnoList().setWidget(i, 0, checkbox);
					anno_list.getImageAnnoList().setWidget(i, 1, 
							new Label(anno.label()));
					
					i++;
					
				} else if (anno.body().isText()) {
					// check if the text annotation is targeted
					if (AnnotationUtil.isSpecificResource(anno)) {
						Label text = new Label(anno.label());
						text.setStylePrimaryName("AnnotationLabel");
						
						anno_list.getTargetedTextAnnoList().setWidget(k, 0, checkbox);
						anno_list.getTargetedTextAnnoList().setWidget(k, 1, text);
						k++;
					}
				}
				
				checkbox.setValue(data.getAnnotationStatus(anno), false);
				bind_annotation_checkbox(checkbox, anno);
			}
		}
		
		display.getAnnoListWidget().getShowAnnoButton().addClickHandler(
        		new ClickHandler() {
        			public void onClick(ClickEvent event) {
        				select_all_annotations(true);
        			}
        		});
        
        display.getAnnoListWidget().getHideAnnoButton().addClickHandler(
        		new ClickHandler() {
        			public void onClick(ClickEvent event) {
        				select_all_annotations(false);
        			}
        		});
		
		anno_list_ready = true;
    }
    
    /**
     * Setup the Text Annotations menu and add data
     */
    private void set_text_annotations_list() {
    	if (data == null) {
    		return;
    	}
    	
    	PopupPanel text_popup = display.getTextAnnotationsPopup();
    	FlowPanel main = (FlowPanel) text_popup.getWidget();
    	StackLayoutPanel tab_panel = (StackLayoutPanel) main.getWidget(1);
    	
    	tab_panel.clear();
    	
    	List<AnnotationList> annotation_lists = data.getAnnotationLists();
    	if (annotation_lists.size() == 0) {
    		return;
    	}
    	
    	double header_size = 35;
    	
    	for (AnnotationList al : annotation_lists) {
    		for (Annotation ann : al) {
    			if (!ann.body().isText() || AnnotationUtil.isSpecificResource(ann)) {
    				continue;
    			}
    			
    			String text = ann.body().textContent();
    			String label = ann.label();
    			
    			if (ann.body().format().endsWith("xml")) {
    				String[] cont = { text };
    				String[] name = { label };
    				
    				// Grab all tabs and put them into tab_panel
    				TabLayoutPanel tab = TranscriptionViewer.createTranscriptionViewer(
    						cont, name, 200, false);
    				
    				// Transfer all tabs to the Text Popup's TabLayoutPanel
    				for (int i = 0; i < tab.getWidgetCount(); i++) {
    					Widget tab_widget = tab.getTabWidget(i);
    					Widget content_widget = tab.getWidget(i);
    					
    					String tab_text = tab_widget.getElement().getInnerHTML();
    					HTML content = new HTML(
    							content_widget.getElement().getInnerHTML());
    					
    					ScrollPanel scroll = new ScrollPanel();
    					
    					scroll.setWidth("97%");
    					scroll.setHeight("97%");
    					scroll.add(content);
    					
    					content.addStyleName("TextAnnoTabPanel");
    					tab_panel.add(scroll, tab_text, header_size);
    				}
    				
    			} else {
    				ScrollPanel scroll = new ScrollPanel();
    				HTML content = new HTML(text);
    				
    				scroll.setWidth("97%");
    				scroll.setHeight("97%");
    				scroll.add(content);
    				
    				tab_panel.add(scroll, label, header_size);
    			}
    		}
    	}
    	
    	text_list_ready = true;
    }
    
    /**
     * Setup the title bar context, with clickable links
     */
    private void set_context() {
    	final ManifestCollection collection = data.getManifestCollection();
    	final Manifest manifest = data.getManifest();
    	final Sequence seq = data.getSequence();
    	final Canvas canvas = data.getCanvas();
    	
    	display.clearContextLabels();
    	
    	if (collection != null) {
    		Label context = display.addContextLabel(collection.label());
    		
    		if (manifest != null) {
    			context.addStyleName("Link");
	    		context.addClickHandler(new ClickHandler() {
	    			public void onClick(ClickEvent event) {
	    				PanelState state = new PanelState(PanelView.MANIFEST_COLLECTION,
		    					collection.uri());
		    			PanelRequestEvent req = new PanelRequestEvent(
		    					PanelRequestEvent.PanelAction.CHANGE, panel_id,
		    					state);
		    			event_bus.fireEvent(req);
	    			}
	    		});
    		}
    	}
    	
    	if (manifest != null) {
    		Label context = display.addContextLabel(manifest.label());
    		
    		if (seq != null) {
    			context.addStyleName("Link");
	    		context.addClickHandler(new ClickHandler() {
	    			public void onClick(ClickEvent event) {
	    				PanelState state = new PanelState(PanelView.MANIFEST,
	    						manifest.uri());
	    				PanelRequestEvent req = new PanelRequestEvent(
	    						PanelRequestEvent.PanelAction.CHANGE, panel_id,
	    						state);
	    				event_bus.fireEvent(req);
	    			}
	    		});
    		}
    	}
    	
    	if (seq != null) {
    		Label context = display.addContextLabel(seq.label());
    		
    		if (canvas != null) {
    			context.addStyleName("Link");
	    		context.addClickHandler(new ClickHandler() {
	    			public void onClick(ClickEvent event) {
	    				PanelState state = new PanelState(PanelView.SEQUENCE,
	    						seq.uri(), manifest.uri());
	    				PanelRequestEvent req = new PanelRequestEvent(
	    						PanelRequestEvent.PanelAction.CHANGE, panel_id,
	    						state);
	    				event_bus.fireEvent(req);
	    			}
	    		});
    		}
    	}
    	
    	if (canvas != null) {
    		Label context = display.addContextLabel(canvas.label());
    	}
    }
    
    /**
     * Defines the behavior of the checkboxes in the List of Annotations. 
     * By default, the checkboxes do nothing. This must be overridden in any
     * class that uses the checkboxes.
     * 
     * @param checkbox
     * @param ann
     */
    public void bind_annotation_checkbox(CheckBox checkbox, Annotation ann) {
		
    }
    
    /**
     * Display or hide all annotations, excluding text annotations that target
     * the canvas as a whole.
     * 
     * @param status
     */
    private void select_all_annotations(boolean status) {
    	FlexTable image_list = display.getAnnoListWidget().getImageAnnoList();
		FlexTable text_list = display.getAnnoListWidget().getTargetedTextAnnoList();
		
		for (int i = 0; i < image_list.getRowCount(); i++) {
			CheckBox check = (CheckBox) image_list.getWidget(i, 0);
			check.setValue(status, true);
		}
		
		for (int i = 0; i < text_list.getRowCount(); i++) {
			CheckBox check = (CheckBox) text_list.getWidget(i, 0);
			check.setValue(status, true);
		}
    }
    
    @Override
    public Widget asWidget() {
        return display.asWidget();
    }

    @Override
    public void resize(int width, int height) {
        display.resize(width, height);
    }
    
    @Override
    public void selected(boolean is_selected) {
    	display.selected(is_selected);
    }
}
