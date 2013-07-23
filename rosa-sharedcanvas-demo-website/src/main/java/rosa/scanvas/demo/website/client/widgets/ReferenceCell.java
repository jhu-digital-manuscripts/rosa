package rosa.scanvas.demo.website.client.widgets;

import rosa.scanvas.model.client.Reference;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class ReferenceCell<T> extends AbstractCell<Reference<T>> {
    public void render(com.google.gwt.cell.client.Cell.Context context,
            Reference<T> ref, SafeHtmlBuilder builder) {
        builder.appendEscaped(ref.label());
    }
}
