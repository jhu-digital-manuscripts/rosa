package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.demo.website.client.Messages;
import rosa.scanvas.demo.website.client.presenter.BasePanelPresenter;
import rosa.scanvas.demo.website.client.widgets.AnnotationListWidget;
import rosa.scanvas.demo.website.client.widgets.ManifestListWidget;

import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.user.client.Window;

public class BasePanelView extends Composite implements BasePanelPresenter.Display {

	private class MovingPopupPanel extends PopupPanel {
		
		private Widget parent_button;

		public MovingPopupPanel(Widget parent_button, 
				boolean autohide, boolean modal) {
			super(autohide, modal);
			this.parent_button = parent_button;
			setPreviewingAllNativeEvents(true);
		}

		@Override
		protected void onPreviewNativeEvent(NativePreviewEvent event) {
			// Hook the popup panel's event preview.
			if (!event.isCanceled()) {

				EventTarget target = event.getNativeEvent().getEventTarget();
				Element parent_element = parent_button.getElement();
				
				switch (event.getTypeInt()) {

				// OnScroll events are not previewable!
				/*case Event.ONSCROLL:
					setPopupPosition(options_button.getAbsoluteLeft() 
							+ options_button.getOffsetWidth()
							- getOffsetWidth(), 
							options_button.getAbsoluteTop() 
							+ options_button.getOffsetHeight());
					return;

				case Event.ONMOUSEWHEEL:
					final int left = options_button.getAbsoluteLeft() 
							+ options_button.getOffsetWidth()
							- getOffsetWidth();
					final int top = options_button.getAbsoluteTop() 
							+ options_button.getOffsetHeight();
					
					if (top + getOffsetHeight() > Window.getClientHeight()) {
						return;
					}
					
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {    
						@Override
						public void execute() {
							setPopupPosition(left, top);
						}
					});

					return;*/
					
				case Event.ONMOUSEDOWN:
					// Check to see if the target is the current popup
					if (parent_element.isOrHasChild(Element.as(target))) {
						event.cancel();
						hide();
						return;
					}
					super.onPreviewNativeEvent(event);
					return;
					
				case Event.ONTOUCHSTART:
					if (parent_element.isOrHasChild(Element.as(target))) {
						event.cancel();
						hide();
						return;
					}
					super.onPreviewNativeEvent(event);
					return;
				}
				super.onPreviewNativeEvent(event);
			}
		}
	}


	private final FlowPanel main;
	private final FlowPanel title_bar;
	private final FlowPanel context_bar;
	private final FlowPanel context_links;

	private AnnotationListWidget annoListWidget;
	private ManifestListWidget metaListWidget;

	private final ToggleButton options_button;
	private final ToggleButton anno_button;
	private final ToggleButton meta_button;
	private final ToggleButton text_button;

	private final PopupPanel text_popup;
	private final PopupPanel meta_popup;
	private final PopupPanel anno_popup;
	private final PopupPanel options_popup;
	private final PopupPanel hide_all;

	//private final HTML hide_all_child;
	
	private final Label close;
	private final Label swap_h;
	private final Label dupl;
	private final Label text_header;
	
	private final PushButton close_button;
	private final PushButton swap_h_button;
	private final PushButton dupl_button;
	private final PushButton move_up;
	private final PushButton move_down;

	private final StackLayoutPanel tab_panel;
	
	public BasePanelView() {
		main = new FlowPanel();
		title_bar = new FlowPanel();
		context_bar = new FlowPanel();
		context_links = new FlowPanel();
		main.setStylePrimaryName("PanelView");
		title_bar.setStylePrimaryName("PanelTitleBar");
		context_bar.setStylePrimaryName("ContextBar");
		context_links.setStylePrimaryName("ContextLinks");
		
		tab_panel = new StackLayoutPanel(Style.Unit.PX);
		tab_panel.setStylePrimaryName("StackLayoutPanel");

		annoListWidget = new AnnotationListWidget();
		metaListWidget = new ManifestListWidget();

		Image options_image_up = new Image("icons/cog_grey.png");
		Image options_image_down = new Image("icons/cog_black.png");
		options_button = new ToggleButton(options_image_up, options_image_down);

		Image anno_image_up = new Image("icons/list grey.png");
		Image anno_image_down = new Image("icons/list black.png");
		anno_button = new ToggleButton(anno_image_up, anno_image_down);

		Image meta_image_up = new Image("icons/i grey.png");
		Image meta_image_down = new Image("icons/i black.png");
		meta_button = new ToggleButton(meta_image_up, meta_image_down);

		Image text_image_up = new Image("icons/asterisk grey.png");
		Image text_image_down = new Image("icons/asterisk black.png");
		text_button = new ToggleButton(text_image_up, text_image_down);

		options_button.addStyleName("OptionsButton");
		anno_button.addStyleName("AnnotationsButton");
		meta_button.addStyleName("MetadataButton");
		text_button.addStyleName("TextAnnotationsButton");
		
		options_button.setEnabled(false);
		anno_button.setEnabled(false);
		meta_button.setEnabled(false);
		text_button.setEnabled(false);
		
		text_popup = new MovingPopupPanel(text_button, true, false);
		meta_popup = new MovingPopupPanel(meta_button, true, false);
		anno_popup = new MovingPopupPanel(anno_button, true, false);
		options_popup = new MovingPopupPanel(options_button, true, false);
		hide_all = new PopupPanel(false, false);
		
		text_popup.setStylePrimaryName("PopupPanel");
		meta_popup.setStylePrimaryName("PopupPanel");
		anno_popup.setStylePrimaryName("PopupPanel");
		options_popup.setStylePrimaryName("PopupPanel");

		close = new Label(Messages.INSTANCE.close());
		swap_h = new Label(Messages.INSTANCE.swap());
		dupl = new Label(Messages.INSTANCE.duplicate());
		text_header = new Label(Messages.INSTANCE.textHeader());
		text_header.addStyleName("TitleHeader");
		
		close_button = new PushButton(new Image("icons/close.png"));
		swap_h_button = new PushButton(new Image("icons/swap lr.png"));
		dupl_button = new PushButton(new Image("icons/duplicate.png"));
		move_up = new PushButton(new Image("icons/up-arrow-inv.png"));
		move_down = new PushButton(new Image("icons/down-arrow-inv.png"));
		
		main.add(title_bar);
		title_bar.add(context_bar);
		title_bar.add(text_button);
		title_bar.add(anno_button);
		title_bar.add(meta_button);
		title_bar.add(options_button);
		title_bar.add(context_links);

		initWidget(main);
		
		setup_options();
		setup_annotations_list();
		setup_meta_list();
		setup_text_annotations();
		
		hide_all.setStylePrimaryName("HidePanel");
		//hide_all_child = new HTML("<i>Loading...</i>");
		Image hide_all_child = new Image("images/loading.gif");
		hide_all.setWidget(hide_all_child);

		this.addAttachHandler(new AttachEvent.Handler() {
			public void onAttachOrDetach(AttachEvent event) {
				if (!event.isAttached()) {
					anno_popup.hide();
					meta_popup.hide();
					text_popup.hide();
					options_popup.hide();
				}
			}
		});
	}

	private void setup_annotations_list() {
		ScrollPanel top = new ScrollPanel();
		FlowPanel main = new FlowPanel();

		top.add(main);

		Label header = new Label(Messages.INSTANCE.annotationsHeader());
		header.addStyleName("TitleHeader");

		main.add(header);
		main.add(annoListWidget);

		anno_popup.setWidget(top);

		anno_popup.addAttachHandler(new AttachEvent.Handler() {
			public void onAttachOrDetach(AttachEvent event) {
				anno_popup.setPopupPosition(options_button.getAbsoluteLeft() 
						+ options_button.getOffsetWidth()
						- anno_popup.getOffsetWidth(), 
						anno_button.getAbsoluteTop() 
						+ anno_button.getOffsetHeight());
				anno_popup.setVisible(true);
			}
		});
		
		anno_popup.addCloseHandler(new CloseHandler() {
			public void onClose(CloseEvent event) {
				anno_button.setDown(false);
			}
		});
	}

	private void setup_meta_list() {
		ScrollPanel top = new ScrollPanel();
		FlowPanel main = new FlowPanel();

		top.add(main);

		Label header = new Label(Messages.INSTANCE.metadataHeader());
		header.addStyleName("TitleHeader");

		main.add(header);
		main.add(metaListWidget);

		meta_popup.setWidget(top);

		meta_popup.addAttachHandler(new AttachEvent.Handler() {
			public void onAttachOrDetach(AttachEvent event) {
				meta_popup.setPopupPosition(options_button.getAbsoluteLeft() 
						+ options_button.getOffsetWidth()
						- meta_popup.getOffsetWidth(), 
						meta_button.getAbsoluteTop() 
						+ meta_button.getOffsetHeight());
				meta_popup.setVisible(true);
			}
		});
		
		meta_popup.addCloseHandler(new CloseHandler() {
			public void onClose(CloseEvent event) {
				meta_button.setDown(false);
			}
		});
	}

	private void setup_text_annotations() {
		FlowPanel main = new FlowPanel();

		main.add(text_header);
		main.add(tab_panel);

		text_popup.setWidget(main);
		
		text_popup.addAttachHandler(new AttachEvent.Handler() {
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					text_popup.setPopupPosition(options_button.getAbsoluteLeft() 
							+ options_button.getOffsetWidth()
							- text_popup.getOffsetWidth(), 
							text_button.getAbsoluteTop() 
							+ text_button.getOffsetHeight());
					text_popup.setVisible(true);
					
					tab_panel.setHeight((text_popup.getOffsetHeight()
							- text_header.getOffsetHeight() - 16) + "px");
				}
			}
		});
		
		text_popup.addCloseHandler(new CloseHandler() {
			public void onClose(CloseEvent event) {
				text_button.setDown(false);
			}
		});
	}

	private void setup_options() {
		FlowPanel main = new FlowPanel();
		options_popup.setWidget(main);

		Label header = new Label(Messages.INSTANCE.optionsHeader());
		header.addStyleName("TitleHeader");

		FlowPanel swap_up = new FlowPanel();
		Label move = new Label(Messages.INSTANCE.moveUp());
		move.addStyleName("Swap");

		swap_up.add(move_up);
		swap_up.add(move);
		
		FlowPanel swap_down = new FlowPanel();
		Label move_d = new Label(Messages.INSTANCE.moveDown());
		move_d.addStyleName("Swap");
		
		swap_down.add(move_down);
		swap_down.add(move_d);
		
		FlowPanel close_panel = new FlowPanel();
		close_panel.add(close_button);
		close_panel.add(close);
		close.addStyleName("Swap");
		
		FlowPanel dupl_panel = new FlowPanel();
		dupl_panel.add(dupl_button);
		dupl_panel.add(dupl);
		dupl.addStyleName("Swap");
		
		FlowPanel swap_h_panel = new FlowPanel();
		swap_h_panel.add(swap_h_button);
		swap_h_panel.add(swap_h);
		swap_h.addStyleName("Swap");

		Grid options_grid = new Grid(5,1);

		options_grid.addStyleName("Options");
		options_grid.setWidget(0, 0, dupl_panel);
		options_grid.setWidget(1, 0, swap_h_panel);
		options_grid.setWidget(2, 0, swap_up);
		options_grid.setWidget(3, 0, swap_down);
		options_grid.setWidget(4, 0, close_panel);

		for (int i = 0; i < options_grid.getRowCount(); i++) {
			options_grid.getWidget(i, 0).addStyleName("OptionsRow");
		}

		main.add(header);
		main.add(options_grid);

		options_popup.addAttachHandler(new AttachEvent.Handler() {
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					options_popup.setWidth(135 + "px");

					options_popup.setPopupPosition(options_button.getAbsoluteLeft() 
							+ options_button.getOffsetWidth()
							- options_popup.getOffsetWidth(),
							options_button.getAbsoluteTop() 
							+ options_button.getOffsetHeight());
					options_popup.setVisible(true);
				}
			}
		});
		
		options_popup.addCloseHandler(new CloseHandler() {
			public void onClose(CloseEvent event) {
				options_button.setDown(false);
			}
		});
	}
	
	@Override
	public Label addContextTitle(String text) {
		// TODO make this more readable for longer context titles
		if (context_bar.getWidgetCount() > 0) {
			context_bar.add(new Label(Messages.INSTANCE.contextSeparator()));
		}

		Label context = new Label(text);
		context_bar.add(context);

		return context;
	}
	
	@Override
	public Label addContextLink(String text) {
		if (context_links.getWidgetCount() > 0) {
			context_links.add(new Label(Messages.INSTANCE.contextSeparator()));
		}
		
		Label context = new Label(text);
		context_links.add(context);
		
		return context;
	}
	
	@Override
	public void clearContextLabels() {
		context_bar.clear();
		context_links.clear();
	}

	/**
	 * Adds a new widget to the content area
	 * 
	 * @param content
	 */
	public void addContent(Widget content) {
		main.add(content);
	}

	@Override
	public ToggleButton getOptionsButton() {
		return options_button;
	}

	@Override
	public ToggleButton getAnnotationsButton() {
		return anno_button;
	}

	@Override
	public ToggleButton getMetadataButton() {
		return meta_button;
	}

	@Override
	public ToggleButton getTextAnnotationsButton() {
		return text_button;
	}

	@Override
	public PopupPanel getOptionsPopup() {
		return options_popup;
	}

	@Override
	public PopupPanel getAnnotationsPopup() {
		return anno_popup;
	}

	@Override
	public PopupPanel getMetadataPopup() {
		return meta_popup;
	}

	@Override
	public PopupPanel getTextAnnotationsPopup() {
		return text_popup;
	}

	@Override
	public HasClickHandlers getCloseButton() {
		return close_button;
	}
	
	@Override
	public HasClickHandlers getCloseLabel() {
		return close;
	}

	@Override
	public HasClickHandlers getDuplicateButton() {
		return dupl_button;
	}
	
	@Override
	public HasClickHandlers getDuplicateLabel() {
		return dupl;
	}

	@Override
	public HasClickHandlers getSwapHorizontalButton() {
		return swap_h_button;
	}
	
	@Override
	public HasClickHandlers getSwapHorizontalLabel() {
		return swap_h;
	}

	@Override
	public HasClickHandlers getMoveUpButton() {
		return move_up;
	}

	@Override
	public HasClickHandlers getMoveDownButton() {
		return move_down;
	}

	@Override
	public AnnotationListWidget getAnnoListWidget() {
		return annoListWidget;
	}

	@Override
	public ManifestListWidget getMetaListWidget() {
		return metaListWidget;
	}

	@Override
	public Widget asWidget() {
		return this;
	}
	
	@Override
	public int getContextHeight() {
		return context_links.getOffsetHeight();
	}
	
	@Override
	public void hideContent(int width, int height) {
		hide_all.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop());
		hide_all.show();
		hide_all.setWidth(width + "px");
		hide_all.setHeight(height + "px");
	}
	
	@Override
	public void showContent() {
		hide_all.hide();
	}

	@Override
	public void resize(int width, int height) {
		setPixelSize(width, height);
		
		hide_all.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop());
		hide_all.setWidth(width + "px");
		hide_all.setHeight(height + "px");

		context_bar.setWidth(width - text_button.getOffsetWidth()
				- meta_button.getOffsetWidth() - anno_button.getOffsetWidth()
				- options_button.getOffsetWidth() + "px");
		context_bar.setHeight(options_button.getOffsetHeight() + "px");

		int popup_top = options_button.getAbsoluteTop() + options_button.getOffsetHeight();
		int right = options_button.getAbsoluteLeft() + options_button.getOffsetWidth();

		// set popup position
		text_popup.setPopupPosition((right - text_popup.getOffsetWidth()), popup_top);
		meta_popup.setPopupPosition((right - meta_popup.getOffsetWidth()), popup_top);
		anno_popup.setPopupPosition((right - anno_popup.getOffsetWidth()), popup_top);
		options_popup.setPopupPosition((right - options_popup.getOffsetWidth()), popup_top);

		width = (int) (width * 0.50);
		height = height - options_button.getOffsetHeight() - 15;

		if (text_popup.getWidget() != null) {
			text_popup.getWidget().setWidth(width + "px");
			text_popup.getWidget().setHeight(height + "px");
		}

		if (meta_popup.getWidget() != null) {
			meta_popup.getWidget().setWidth(width + "px");
			meta_popup.getWidget().setHeight(height + "px");
		}

		if (anno_popup.getWidget() != null) {
			anno_popup.getWidget().setWidth(width + "px");
			anno_popup.getWidget().setHeight(height + "px");
		}

		tab_panel.setWidth((int) (width * 0.98) + "px");
		tab_panel.setHeight((height - text_header.getOffsetHeight()) + "px");
	}
}
