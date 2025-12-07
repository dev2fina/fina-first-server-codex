package net.fina.common.server.util;

import java.io.*;

public class ObjectSerializer {
    public static byte[] serialize(Object obj) throws IOException {
        byte[] result = null;
        try (ByteArrayOutputStream b = new ByteArrayOutputStream(); ObjectOutputStream o = new ObjectOutputStream(b)) {
            o.writeObject(obj);
            result = b.toByteArray();
        }
        return result;
    }

    public static Object deSerialize(byte[] bytes) throws IOException, ClassNotFoundException {
        Object result = null;
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes); ObjectInputStream o = new ObjectInputStream(b)) {
            result = o.readObject();
        }
        return result;
    }

    public static <T> T deSerialize(byte[] bytes, Class<T> cast) throws IOException, ClassNotFoundException {
        Object result = null;
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes); ObjectInputStream o = new ObjectInputStream(b)) {
            result = o.readObject();
        }
        return (T) result;
    }
}