package rosa.gwt.common.client;

// Playing with CellBrowser...

//public class BookPickerTreeModel implements TreeViewModel {
//	private final BookCollection col;
//	private final ListDataProvider<CategoryValue> catvalues;
//
//	public BookPickerTreeModel(BookCollection col, BookCollection.Category category) {
//		this.col = col;
//		this.catvalues = new ListDataProvider<BookPickerTreeModel.CategoryValue>();
//
//		Map<String, List<Integer>> result = col.browse(category);
//
//		for (Map.Entry<String, List<Integer>> entry : result.entrySet()) {
//			catvalues.getList().add(
//					new CategoryValue(entry.getKey(), entry.getValue()));
//		}
//
//		if (category == BookCollection.Category.NUM_FOLIOS
//				|| category == BookCollection.Category.NUM_ILLUSTRATIONS) {
//			// numeric sort
//
//			Collections.sort(catvalues.getList(),
//					new Comparator<CategoryValue>() {
//						public int compare(CategoryValue o1, CategoryValue o2) {
//							try {
//								return Integer.parseInt(o1.value)
//										- Integer.parseInt(o2.value);
//							} catch (NumberFormatException e) {
//								return o1.value.compareTo(o2.value);
//							}
//						}
//					});
//		} else {
//			Collections.sort(catvalues.getList(),
//					new Comparator<CategoryValue>() {
//						public int compare(CategoryValue o1, CategoryValue o2) {
//							return Util
//									.compareStringsPossiblyEndingWithNumbers(
//											o1.value, o2.value);
//						}
//					});
//		}
//
//	}
//
//	private static class CategoryValue {
//		String value;
//		List<Integer> bookids;
//
//		public CategoryValue(String value, List<Integer> bookids) {
//			this.value = value;
//			this.bookids = bookids;
//		}
//
//		public boolean equals(Object o) {
//			return this == o;
//		}
//
//		public int hashCode() {
//			return value.hashCode();
//		}
//	}
//
//	private static class CategoryValueCell extends AbstractCell<CategoryValue> {
//		public void render(CategoryValue catvalue, Object key,
//				SafeHtmlBuilder sb) {
//			if (catvalue != null) {
//				sb.appendEscaped(catvalue.value);
//				if (catvalue.bookids.size() > 1) {
//					sb.appendEscaped(" (" + catvalue.bookids.size() + ")");
//				}
//			}
//		}
//	}
//
//	private class BookValueCell extends AbstractCell<Integer> {
//		public void render(Integer bookid, Object key, SafeHtmlBuilder sb) {
//			if (bookid != null) {
//				String id = col.bookData(bookid, BookCollection.Category.ID);
//				String fullname = col.fullBookName(bookid);				
//						
//				sb.append(SafeHtmlUtils.fromTrustedString("<a href='#" + URL.encode(Action.VIEW_BOOK.toToken(id)) + "'>"));
//				sb.appendEscaped(fullname);
//				sb.appendHtmlConstant("</a>");
//			}
//		}
//	}
//
//	public <T> NodeInfo<?> getNodeInfo(T value) {
//		if (value == null) {
//			return new DefaultNodeInfo<CategoryValue>(catvalues,
//					new CategoryValueCell());
//		} else if (value instanceof CategoryValue) {
//			List<Integer> bookids = ((CategoryValue) value).bookids;
//			ListDataProvider<Integer> book_values = new ListDataProvider<Integer>(
//					bookids);
//			return new DefaultNodeInfo<Integer>(book_values,
//					new BookValueCell());
//		}
//
//		return null;
//	}
//
//	public boolean isLeaf(Object value) {
//		return value instanceof Integer;
//	}
// }
