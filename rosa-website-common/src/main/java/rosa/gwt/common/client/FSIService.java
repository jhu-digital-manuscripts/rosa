package rosa.gwt.common.client;

import rosa.gwt.common.client.data.Book;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;

public class FSIService {
    // TODO config
    public static final String SERVER_URL = "http://fsiserver.library.jhu.edu/";

    public static String embedStaticImage(String share, String image, int width, int height) {
        String path = share + "/" + Book.bookIDFromImage(image) + "/" + image;
        String params = "source=" + URL.encodeQueryString(path) + "&width="
                + width + "&height=" + height;
        String url = SERVER_URL + "server?type=image&" + params;
        String html = "<img src='" + url + "'></img>";

        return html;
    }
    
    private static String langparam(String lc) {
        if (lc.equals("en")) {
            return "&language=english";
        } else if (lc.equals("fr")) {
            return "&language=french";
        } else {
            return "";
        }
    }

    public static String embedDynamicImage(String share, String image, String width,
					   String height, String lc) {
        String path = share + "/" + Book.bookIDFromImage(image) + "/" + image;

        String params = "FPXSrc=" + URL.encodeQueryString(path) + langparam(lc);
        String url = SERVER_URL + "viewer/fsi.swf?" + params;

        String html = "<OBJECT CLASSID='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' CODEBASE='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0' WIDTH='"
                + width
                + "' HEIGHT='"
                + height
                + "' ID='theMovie'>"
                + "<PARAM NAME='MENU' VALUE='FALSE'>"
                + "<PARAM NAME='wmode' value='opaque'>"
                + "<PARAM NAME='swliveconnect' VALUE='true'>"
                + "<PARAM NAME='allowscriptaccess' VALUE='always'>"
                + "<PARAM NAME='allowfullscreen' VALUE='always'>"
                + "<PARAM NAME='SRC' VALUE='"
                + url
                + "'>"
                + "<EMBED swliveconnect='true' allowscriptaccess='always' allowfullscreen='true' SRC='"
                + url
                + "' MENU='false' wmode='opaque' PLUGINSPAGE='http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash' WIDTH='"
                + width
                + "' HEIGHT='"
                + height
                + "' TYPE='application/x-shockwave-flash'> </EMBED>"
                + "</OBJECT>";

        return html;
    }

    public static String embedFSIShowcase(String book, int image, String width,
					  String height, String lc) {
        String cfgpath = GWT.getHostPageBaseURL() + "/data/" + book + "/"
                + book + ".showcase.fsi";

        // fsi image number seems to start at 1
        image++;
        // showcase notepad id must be different
        String params = "cfg=" + cfgpath + "&plugins=notepad&notepad_UniqueID="
                + "showcase." + book
                + langparam(lc)
                // Cannot set this while also allowing fullscreen
                // + "&resize_EnlargeBy=100"
                + "&TileSizeX=600&TileSizeY=600" + "&showcase_InitialImage="
                + image;

        String url = SERVER_URL + "viewer/fsi.swf?" + params;

        String html = "<OBJECT id='fsishowcase' CLASSID='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' CODEBASE='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,65,0' WIDTH='"
                + width
                + "' HEIGHT='"
                + height
                + "'>"
                + "<PARAM NAME='menu' VALUE='false'>"
                + "<PARAM NAME='wmode' value='opaque'>"
                + "<PARAM NAME='swliveconnect' VALUE='true'>"
                + "<PARAM NAME='allowscriptaccess' VALUE='always'>"
                + "<PARAM NAME='allowfullscreen' VALUE='true'>"
                + "<PARAM NAME='movie' VALUE='"
                + url
                + "'>"
                + "<EMBED NAME='fsishowcase' swliveconnect='true' allowscriptaccess='always' allowfullscreen='true' SRC='"
                + url
                + "' MENU='false' wmode='opaque' PLUGINSPAGE='http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash' WIDTH='"
                + width
                + "' HEIGHT='"
                + height
                + "' TYPE='application/x-shockwave-flash'> </EMBED>"
                + "</OBJECT>";

        return html;
    }

    public static String embedFSIPages(String book, int image, String width,
				       String height, String lc) {
        String cfgpath = GWT.getHostPageBaseURL() + "/data/" + book + "/"
                + book + ".pages.fsi";

        // pages start at 1
        image++;

        String url = SERVER_URL
                + "viewer/fsi.swf?cfg="
                + cfgpath
                + langparam(lc)
                // Cannot set this while also allowing fullscreen
                // + "&resize_EnlargeBy=100"
                + "&TileSizeX=600&TileSizeY=600"
                + "&pages_ForceInitialPage="
                + image
                + "&pages_Events=true&pages_PageNumbers=false&plugins=notepad&notepad_UniqueID="
                + book;

        String html = "<OBJECT id='fsipages' CLASSID='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' CODEBASE='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,65,0' WIDTH='"
                + width
                + "' HEIGHT='"
                + height
                + "'>"
                + "<PARAM NAME='menu' VALUE='false'>"
                + "<PARAM NAME='wmode' value='opaque'>"
                + "<PARAM NAME='swliveconnect' VALUE='true'>"
                + "<PARAM NAME='allowscriptaccess' VALUE='always'>"
                + "<PARAM NAME='allowfullscreen' VALUE='true'>"
                + "<PARAM NAME='movie' VALUE='"
                + url
                + "'>"
                + "<EMBED NAME='fsipages' swliveconnect='true' allowscriptaccess='always' allowfullscreen='true' SRC='"
                + url
                + "' MENU='false' wmode='opaque' PLUGINSPAGE='http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash' WIDTH='"
                + width
                + "' HEIGHT='"
                + height
                + "' TYPE='application/x-shockwave-flash'></EMBED>"
                + "</OBJECT>";

        return html;
    }

    public interface FSIPagesCallback {
        void pageChanged(int page);

        void imageInfo(String info);
    }

    public interface FSIShowcaseCallback {
        void imageSelected(int image);
    }

    // Flash object must have fsipages id
    public static native void setupFSIPagesCallback(FSIPagesCallback cb) /*-{
                                                                         $wnd.fsipages_DoFSCommand = function (fsievent, args) {
                                                                         switch (fsievent) {
                                                                         case "ImageInfo":
                                                                         cb.@rosa.gwt.common.client.FSIService.FSIPagesCallback::imageInfo(Ljava/lang/String;)(args);
                                                                         break;
                                                                         
                                                                         case "onPagesPageChanged":
                                                                         cb.@rosa.gwt.common.client.FSIService.FSIPagesCallback::pageChanged(I)(args);
                                                                         break;
                                                                         }
                                                                         }
                                                                         }-*/;

    // Flash object must have fsishowcase id
    public static native void setupFSIShowcaseCallback(FSIShowcaseCallback cb) /*-{
                                                                               $wnd.fsishowcase_DoFSCommand = function (fsievent, args) {
                                                                               switch (fsievent) {
                                                                               case "ImageSelected":
                                                                               cb.@rosa.gwt.common.client.FSIService.FSIShowcaseCallback::imageSelected(I)(args);
                                                                               break;
                                                                               }
                                                                               }
                                                                               }-*/;

    /**
     * @return 0 based index for image in config file
     */
    public static int getImageIndexFromShowcaseInfo(String info) {
        String marker = "ImageIndex=";
        int i = info.indexOf(marker);

        if (i == -1) {
            return 0;
        }

        return Integer.parseInt(info.substring(i + marker.length()));
    }

    /**
     * @return 0 based index for image in config file
     */
    public static int getImageIndexFromPagesInfo(String info) {
        int result = getImageIndexFromShowcaseInfo(info) - 1;

        if (result < 0) {
            // TODO this should not happen
            result = 0;
        }

        return result;
    }

    private static native void fsipagesSetVariable(String name, String val) /*-{
                                                                            var fsiobj = $doc.all ? $doc.getElementById('fsipages') : $doc.fsipages;
                                                                            
                                                                            if (fsiobj) {
                                                                            fsiobj.SetVariable(name, val);
                                                                            }
                                                                            }-*/;

    private static native void fsishowcaseSetVariable(String name, String val) /*-{
                                                                               var fsiobj = $doc.all ? $doc.getElementById('fsishowcase') : $doc.fsishowcase;
                                                                               
                                                                               if (fsiobj) {
                                                                               fsiobj.SetVariable(name, val);
                                                                               }
                                                                               }-*/;

    public static void fsipagesGotoImage(int image) {
        fsipagesSetVariable("newImageIndex", "" + image);
        fsipagesSetVariable("FSICMD", "GotoPage");
    }

    public static void fsishowcaseSelectImage(int image) {
        fsishowcaseSetVariable("newImageIndex", "" + image);
        fsishowcaseSetVariable("FSICMD", "SelectImage");
    }
}
