package net.floodlightcontroller.fileuploaddown;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.fileuploaddown.type.FileData;
import net.floodlightcontroller.util.ConcurrentCircularBuffer;

public interface IFileUploadDownService extends IFloodlightService {
    public ConcurrentCircularBuffer<FileData> getBuffer();
}
