package org.telegram.tl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: ex3ndr
 * Date: 25.10.13
 * Time: 16:38
 */
public abstract class TLContext {
    private HashMap<Integer, Class> registeredClasses = new HashMap<Integer, Class>();

    public TLContext() {
        init();
    }

    protected void init() {

    }

    public <T extends TLObject> void registerClass(Class<T> tClass) {
        try {
            int classId = tClass.getField("CLASS_ID").getInt(null);
            registeredClasses.put(classId, tClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public <T extends TLObject> void registerClass(int clazzId, Class<T> tClass) {
        registeredClasses.put(clazzId, tClass);
    }

    public TLObject deserializeMessage(int clazzId, InputStream stream) throws IOException {
        if (clazzId == TLGzipObject.CLASS_ID) {
            TLGzipObject obj = new TLGzipObject();
            obj.deserialize(stream, this);
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(obj.getPackedData()));
            int innerClazzId = StreamingUtils.readInt(gzipInputStream);
            return deserializeMessage(innerClazzId, gzipInputStream);
        }

        try {
            TLObject message = (TLObject) registeredClasses.get(clazzId).getConstructor().newInstance();
            message.deserializeBody(stream, this);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Unable to deserialize data");
        }
    }

    public TLObject deserializeMessage(InputStream stream) throws IOException {
        int clazzId = StreamingUtils.readInt(stream);
        return deserializeMessage(clazzId, stream);
    }

    public TLVector deserializeVector(InputStream stream) throws IOException {
        int clazzId = StreamingUtils.readInt(stream);
        if (clazzId == TLVector.CLASS_ID) {
            TLVector res = new TLVector();
            res.deserializeBody(stream, this);
            return res;
        } else if (clazzId == TLGzipObject.CLASS_ID) {
            TLGzipObject obj = new TLGzipObject();
            obj.deserialize(stream, this);
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(obj.getPackedData()));
            return deserializeVector(gzipInputStream);
        } else {
            throw new IOException("Unable to deserialize vector");
        }
    }

    public TLIntVector deserializeIntVector(InputStream stream) throws IOException {
        int clazzId = StreamingUtils.readInt(stream);
        if (clazzId == TLVector.CLASS_ID) {
            TLIntVector res = new TLIntVector();
            res.deserializeBody(stream, this);
            return res;
        } else if (clazzId == TLGzipObject.CLASS_ID) {
            TLGzipObject obj = new TLGzipObject();
            obj.deserialize(stream, this);
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(obj.getPackedData()));
            return deserializeIntVector(gzipInputStream);
        } else {
            throw new IOException("Unable to deserialize vector");
        }
    }

    public TLLongVector deserializeLongVector(InputStream stream) throws IOException {
        int clazzId = StreamingUtils.readInt(stream);
        if (clazzId == TLVector.CLASS_ID) {
            TLLongVector res = new TLLongVector();
            res.deserializeBody(stream, this);
            return res;
        } else if (clazzId == TLGzipObject.CLASS_ID) {
            TLGzipObject obj = new TLGzipObject();
            obj.deserialize(stream, this);
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(obj.getPackedData()));
            return deserializeLongVector(gzipInputStream);
        } else {
            throw new IOException("Unable to deserialize vector");
        }
    }
}