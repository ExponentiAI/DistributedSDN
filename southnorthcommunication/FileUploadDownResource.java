package net.floodlightcontroller.fileuploaddown;


import net.floodlightcontroller.fileuploaddown.type.FileData;
import org.restlet.resource.ServerResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUploadDownResource extends ServerResource {

    public List<FileData> retrieve() {
        IFileUploadDownService pihr = (IFileUploadDownService)getContext().getAttributes().get(IFileUploadDownService.class.getCanonicalName());
        List<FileData> l = new ArrayList<FileData>();
        l.addAll(Arrays.asList(pihr.getBuffer().snapshot()));
        return l;
    }

}
