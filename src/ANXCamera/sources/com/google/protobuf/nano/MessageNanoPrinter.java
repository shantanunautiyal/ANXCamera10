package com.google.protobuf.nano;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MessageNanoPrinter {
    private static final String INDENT = "  ";
    private static final int MAX_STRING_LEN = 200;

    private MessageNanoPrinter() {
    }

    private static void appendQuotedBytes(byte[] bArr, StringBuffer stringBuffer) {
        if (bArr == null) {
            stringBuffer.append("\"\"");
            return;
        }
        stringBuffer.append('\"');
        for (byte b2 : bArr) {
            byte b3 = b2 & 255;
            if (b3 == 92 || b3 == 34) {
                stringBuffer.append('\\');
                stringBuffer.append((char) b3);
            } else if (b3 < 32 || b3 >= Byte.MAX_VALUE) {
                stringBuffer.append(String.format("\\%03o", new Object[]{Integer.valueOf(b3)}));
            } else {
                stringBuffer.append((char) b3);
            }
        }
        stringBuffer.append('\"');
    }

    private static String deCamelCaseify(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (i == 0) {
                stringBuffer.append(Character.toLowerCase(charAt));
            } else if (Character.isUpperCase(charAt)) {
                stringBuffer.append('_');
                stringBuffer.append(Character.toLowerCase(charAt));
            } else {
                stringBuffer.append(charAt);
            }
        }
        return stringBuffer.toString();
    }

    private static String escapeString(String str) {
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt < ' ' || charAt > '~' || charAt == '\"' || charAt == '\'') {
                sb.append(String.format("\\u%04x", new Object[]{Integer.valueOf(charAt)}));
            } else {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    public static <T extends MessageNano> String print(T t) {
        String str = "Error printing proto: ";
        if (t == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        try {
            print(null, t, new StringBuffer(), stringBuffer);
            return stringBuffer.toString();
        } catch (IllegalAccessException e2) {
            String valueOf = String.valueOf(e2.getMessage());
            return valueOf.length() != 0 ? str.concat(valueOf) : new String(str);
        } catch (InvocationTargetException e3) {
            String valueOf2 = String.valueOf(e3.getMessage());
            return valueOf2.length() != 0 ? str.concat(valueOf2) : new String(str);
        }
    }

    private static void print(String str, Object obj, StringBuffer stringBuffer, StringBuffer stringBuffer2) throws IllegalAccessException, InvocationTargetException {
        Field[] fields;
        if (obj != null) {
            if (obj instanceof MessageNano) {
                int length = stringBuffer.length();
                if (str != null) {
                    stringBuffer2.append(stringBuffer);
                    stringBuffer2.append(deCamelCaseify(str));
                    stringBuffer2.append(" <\n");
                    stringBuffer.append(INDENT);
                }
                Class cls = obj.getClass();
                for (Field field : cls.getFields()) {
                    int modifiers = field.getModifiers();
                    String name = field.getName();
                    if (!"cachedSize".equals(name) && (modifiers & 1) == 1 && (modifiers & 8) != 8) {
                        String str2 = "_";
                        if (!name.startsWith(str2) && !name.endsWith(str2)) {
                            Class type = field.getType();
                            Object obj2 = field.get(obj);
                            if (!type.isArray()) {
                                print(name, obj2, stringBuffer, stringBuffer2);
                            } else if (type.getComponentType() == Byte.TYPE) {
                                print(name, obj2, stringBuffer, stringBuffer2);
                            } else {
                                int length2 = obj2 == null ? 0 : Array.getLength(obj2);
                                for (int i = 0; i < length2; i++) {
                                    print(name, Array.get(obj2, i), stringBuffer, stringBuffer2);
                                }
                            }
                        }
                    }
                }
                for (Method name2 : cls.getMethods()) {
                    String name3 = name2.getName();
                    if (name3.startsWith("set")) {
                        String substring = name3.substring(3);
                        String str3 = "has";
                        try {
                            String valueOf = String.valueOf(substring);
                            if (((Boolean) cls.getMethod(valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3), new Class[0]).invoke(obj, new Object[0])).booleanValue()) {
                                String str4 = "get";
                                String valueOf2 = String.valueOf(substring);
                                print(substring, cls.getMethod(valueOf2.length() != 0 ? str4.concat(valueOf2) : new String(str4), new Class[0]).invoke(obj, new Object[0]), stringBuffer, stringBuffer2);
                            }
                        } catch (NoSuchMethodException unused) {
                        }
                    }
                }
                if (str != null) {
                    stringBuffer.setLength(length);
                    stringBuffer2.append(stringBuffer);
                    stringBuffer2.append(">\n");
                    return;
                }
                return;
            }
            String deCamelCaseify = deCamelCaseify(str);
            stringBuffer2.append(stringBuffer);
            stringBuffer2.append(deCamelCaseify);
            stringBuffer2.append(": ");
            if (obj instanceof String) {
                String sanitizeString = sanitizeString((String) obj);
                String str5 = "\"";
                stringBuffer2.append(str5);
                stringBuffer2.append(sanitizeString);
                stringBuffer2.append(str5);
            } else if (obj instanceof byte[]) {
                appendQuotedBytes((byte[]) obj, stringBuffer2);
            } else {
                stringBuffer2.append(obj);
            }
            stringBuffer2.append("\n");
        }
    }

    private static String sanitizeString(String str) {
        if (!str.startsWith("http") && str.length() > 200) {
            str = String.valueOf(str.substring(0, 200)).concat("[...]");
        }
        return escapeString(str);
    }
}
