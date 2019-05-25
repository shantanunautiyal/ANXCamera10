package com.google.gson.stream;

import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.bind.JsonTreeReader;
import com.ss.android.ttve.common.TEDefine;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class JsonReader implements Closeable {
    private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
    private static final char[] NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
    private static final int NUMBER_CHAR_DECIMAL = 3;
    private static final int NUMBER_CHAR_DIGIT = 2;
    private static final int NUMBER_CHAR_EXP_DIGIT = 7;
    private static final int NUMBER_CHAR_EXP_E = 5;
    private static final int NUMBER_CHAR_EXP_SIGN = 6;
    private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
    private static final int NUMBER_CHAR_NONE = 0;
    private static final int NUMBER_CHAR_SIGN = 1;
    private static final int PEEKED_BEGIN_ARRAY = 3;
    private static final int PEEKED_BEGIN_OBJECT = 1;
    private static final int PEEKED_BUFFERED = 11;
    private static final int PEEKED_DOUBLE_QUOTED = 9;
    private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
    private static final int PEEKED_END_ARRAY = 4;
    private static final int PEEKED_END_OBJECT = 2;
    private static final int PEEKED_EOF = 17;
    private static final int PEEKED_FALSE = 6;
    private static final int PEEKED_LONG = 15;
    private static final int PEEKED_NONE = 0;
    private static final int PEEKED_NULL = 7;
    private static final int PEEKED_NUMBER = 16;
    private static final int PEEKED_SINGLE_QUOTED = 8;
    private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
    private static final int PEEKED_TRUE = 5;
    private static final int PEEKED_UNQUOTED = 10;
    private static final int PEEKED_UNQUOTED_NAME = 14;
    private final char[] buffer = new char[1024];
    private final Reader in;
    private boolean lenient = false;
    private int limit = 0;
    private int lineNumber = 0;
    private int lineStart = 0;
    /* access modifiers changed from: private */
    public int peeked = 0;
    private long peekedLong;
    private int peekedNumberLength;
    private String peekedString;
    private int pos = 0;
    private int[] stack = new int[32];
    private int stackSize = 0;

    static {
        JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
            public void promoteNameToValue(JsonReader reader) throws IOException {
                if (reader instanceof JsonTreeReader) {
                    ((JsonTreeReader) reader).promoteNameToValue();
                    return;
                }
                int p = reader.peeked;
                if (p == 0) {
                    p = reader.doPeek();
                }
                if (p == 13) {
                    reader.peeked = 9;
                } else if (p == 12) {
                    reader.peeked = 8;
                } else if (p == 14) {
                    reader.peeked = 10;
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Expected a name but was ");
                    sb.append(reader.peek());
                    sb.append(" ");
                    sb.append(" at line ");
                    sb.append(reader.getLineNumber());
                    sb.append(" column ");
                    sb.append(reader.getColumnNumber());
                    throw new IllegalStateException(sb.toString());
                }
            }
        };
    }

    public JsonReader(Reader in2) {
        int[] iArr = this.stack;
        int i = this.stackSize;
        this.stackSize = i + 1;
        iArr[i] = 6;
        if (in2 != null) {
            this.in = in2;
            return;
        }
        throw new NullPointerException("in == null");
    }

    public final void setLenient(boolean lenient2) {
        this.lenient = lenient2;
    }

    public final boolean isLenient() {
        return this.lenient;
    }

    public void beginArray() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 3) {
            push(1);
            this.peeked = 0;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Expected BEGIN_ARRAY but was ");
        sb.append(peek());
        sb.append(" at line ");
        sb.append(getLineNumber());
        sb.append(" column ");
        sb.append(getColumnNumber());
        throw new IllegalStateException(sb.toString());
    }

    public void endArray() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 4) {
            this.stackSize--;
            this.peeked = 0;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Expected END_ARRAY but was ");
        sb.append(peek());
        sb.append(" at line ");
        sb.append(getLineNumber());
        sb.append(" column ");
        sb.append(getColumnNumber());
        throw new IllegalStateException(sb.toString());
    }

    public void beginObject() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 1) {
            push(3);
            this.peeked = 0;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Expected BEGIN_OBJECT but was ");
        sb.append(peek());
        sb.append(" at line ");
        sb.append(getLineNumber());
        sb.append(" column ");
        sb.append(getColumnNumber());
        throw new IllegalStateException(sb.toString());
    }

    public void endObject() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 2) {
            this.stackSize--;
            this.peeked = 0;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Expected END_OBJECT but was ");
        sb.append(peek());
        sb.append(" at line ");
        sb.append(getLineNumber());
        sb.append(" column ");
        sb.append(getColumnNumber());
        throw new IllegalStateException(sb.toString());
    }

    public boolean hasNext() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        return (p == 2 || p == 4) ? false : true;
    }

    public JsonToken peek() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        switch (p) {
            case 1:
                return JsonToken.BEGIN_OBJECT;
            case 2:
                return JsonToken.END_OBJECT;
            case 3:
                return JsonToken.BEGIN_ARRAY;
            case 4:
                return JsonToken.END_ARRAY;
            case 5:
            case 6:
                return JsonToken.BOOLEAN;
            case 7:
                return JsonToken.NULL;
            case 8:
            case 9:
            case 10:
            case 11:
                return JsonToken.STRING;
            case 12:
            case 13:
            case 14:
                return JsonToken.NAME;
            case 15:
            case 16:
                return JsonToken.NUMBER;
            case 17:
                return JsonToken.END_DOCUMENT;
            default:
                throw new AssertionError();
        }
    }

    /* access modifiers changed from: private */
    public int doPeek() throws IOException {
        int peekStack = this.stack[this.stackSize - 1];
        if (peekStack == 1) {
            this.stack[this.stackSize - 1] = 2;
        } else if (peekStack == 2) {
            int c = nextNonWhitespace(true);
            if (c != 44) {
                if (c == 59) {
                    checkLenient();
                } else if (c == 93) {
                    this.peeked = 4;
                    return 4;
                } else {
                    throw syntaxError("Unterminated array");
                }
            }
        } else if (peekStack == 3 || peekStack == 5) {
            this.stack[this.stackSize - 1] = 4;
            if (peekStack == 5) {
                int c2 = nextNonWhitespace(true);
                if (c2 != 44) {
                    if (c2 == 59) {
                        checkLenient();
                    } else if (c2 == 125) {
                        this.peeked = 2;
                        return 2;
                    } else {
                        throw syntaxError("Unterminated object");
                    }
                }
            }
            int c3 = nextNonWhitespace(true);
            if (c3 == 34) {
                this.peeked = 13;
                return 13;
            } else if (c3 == 39) {
                checkLenient();
                this.peeked = 12;
                return 12;
            } else if (c3 != 125) {
                checkLenient();
                this.pos--;
                if (isLiteral((char) c3)) {
                    this.peeked = 14;
                    return 14;
                }
                throw syntaxError("Expected name");
            } else if (peekStack != 5) {
                this.peeked = 2;
                return 2;
            } else {
                throw syntaxError("Expected name");
            }
        } else if (peekStack == 4) {
            this.stack[this.stackSize - 1] = 5;
            int c4 = nextNonWhitespace(true);
            if (c4 != 58) {
                if (c4 == 61) {
                    checkLenient();
                    if ((this.pos < this.limit || fillBuffer(1)) && this.buffer[this.pos] == '>') {
                        this.pos++;
                    }
                } else {
                    throw syntaxError("Expected ':'");
                }
            }
        } else if (peekStack == 6) {
            if (this.lenient) {
                consumeNonExecutePrefix();
            }
            this.stack[this.stackSize - 1] = 7;
        } else if (peekStack == 7) {
            if (nextNonWhitespace(false) == -1) {
                this.peeked = 17;
                return 17;
            }
            checkLenient();
            this.pos--;
        } else if (peekStack == 8) {
            throw new IllegalStateException("JsonReader is closed");
        }
        int c5 = nextNonWhitespace(true);
        if (c5 == 34) {
            if (this.stackSize == 1) {
                checkLenient();
            }
            this.peeked = 9;
            return 9;
        } else if (c5 != 39) {
            if (!(c5 == 44 || c5 == 59)) {
                if (c5 == 91) {
                    this.peeked = 3;
                    return 3;
                } else if (c5 != 93) {
                    if (c5 != 123) {
                        this.pos--;
                        if (this.stackSize == 1) {
                            checkLenient();
                        }
                        int result = peekKeyword();
                        if (result != 0) {
                            return result;
                        }
                        int result2 = peekNumber();
                        if (result2 != 0) {
                            return result2;
                        }
                        if (isLiteral(this.buffer[this.pos])) {
                            checkLenient();
                            this.peeked = 10;
                            return 10;
                        }
                        throw syntaxError("Expected value");
                    }
                    this.peeked = 1;
                    return 1;
                } else if (peekStack == 1) {
                    this.peeked = 4;
                    return 4;
                }
            }
            if (peekStack == 1 || peekStack == 2) {
                checkLenient();
                this.pos--;
                this.peeked = 7;
                return 7;
            }
            throw syntaxError("Unexpected value");
        } else {
            checkLenient();
            this.peeked = 8;
            return 8;
        }
    }

    private int peekKeyword() throws IOException {
        int peeking;
        String keywordUpper;
        String keyword;
        char c = this.buffer[this.pos];
        if (c == 't' || c == 'T') {
            keyword = "true";
            keywordUpper = "TRUE";
            peeking = 5;
        } else if (c == 'f' || c == 'F') {
            keyword = "false";
            keywordUpper = "FALSE";
            peeking = 6;
        } else if (c != 'n' && c != 'N') {
            return 0;
        } else {
            keyword = TEDefine.FACE_BEAUTY_NULL;
            keywordUpper = "NULL";
            peeking = 7;
        }
        int length = keyword.length();
        int i = 1;
        while (i < length) {
            if (this.pos + i >= this.limit && !fillBuffer(i + 1)) {
                return 0;
            }
            char c2 = this.buffer[this.pos + i];
            if (c2 != keyword.charAt(i) && c2 != keywordUpper.charAt(i)) {
                return 0;
            }
            i++;
        }
        if ((this.pos + length < this.limit || fillBuffer(length + 1)) && isLiteral(this.buffer[this.pos + length])) {
            return 0;
        }
        this.pos += length;
        this.peeked = peeking;
        return peeking;
    }

    private int peekNumber() throws IOException {
        long value;
        char c;
        long value2;
        char[] buffer2 = this.buffer;
        int p = this.pos;
        boolean negative = false;
        boolean fitsInLong = true;
        char c2 = 0;
        int i = 0;
        long value3 = 0;
        int l = this.limit;
        int p2 = p;
        int i2 = 0;
        while (true) {
            if (p2 + i2 == l) {
                if (i2 == buffer2.length) {
                    return i;
                }
                if (!fillBuffer(i2 + 1)) {
                    value = value3;
                } else {
                    p2 = this.pos;
                    l = this.limit;
                }
            }
            c = buffer2[p2 + i2];
            if (c != '+') {
                if (c != 'E' && c != 'e') {
                    switch (c) {
                        case '-':
                            value2 = value3;
                            if (c2 == 0) {
                                negative = true;
                                c2 = 1;
                                break;
                            } else if (c2 == 5) {
                                c2 = 6;
                                break;
                            } else {
                                return 0;
                            }
                        case '.':
                            int i3 = i;
                            value2 = value3;
                            if (c2 == 2) {
                                c2 = 3;
                                break;
                            } else {
                                return i3;
                            }
                        default:
                            if (c >= '0') {
                                if (c <= '9') {
                                    boolean z = true;
                                    if (c2 != 1) {
                                        if (c2 != 0) {
                                            if (c2 == 2) {
                                                if (value3 != 0) {
                                                    long value4 = value3;
                                                    long newValue = (10 * value3) - ((long) (c - '0'));
                                                    if (value4 <= MIN_INCOMPLETE_INTEGER && (value4 != MIN_INCOMPLETE_INTEGER || newValue >= value4)) {
                                                        z = false;
                                                    }
                                                    fitsInLong &= z;
                                                    value2 = newValue;
                                                    break;
                                                } else {
                                                    return i;
                                                }
                                            } else {
                                                long value5 = value3;
                                                if (c2 == 3) {
                                                    c2 = 4;
                                                } else if (c2 == 5 || c2 == 6) {
                                                    c2 = 7;
                                                }
                                                value3 = value5;
                                                break;
                                            }
                                        } else {
                                            long j = value3;
                                        }
                                    }
                                    value2 = (long) (-(c - '0'));
                                    c2 = 2;
                                    break;
                                } else {
                                    value = value3;
                                    break;
                                }
                            } else {
                                value = value3;
                                break;
                            }
                    }
                } else {
                    int i4 = i;
                    value2 = value3;
                    if (c2 != 2 && c2 != 4) {
                        return i4;
                    }
                    c2 = 5;
                }
            } else {
                value2 = value3;
                if (c2 != 5) {
                    return 0;
                }
                c2 = 6;
            }
            value3 = value2;
            i2++;
            i = 0;
        }
        if (isLiteral(c)) {
            return 0;
        }
        if (c2 != 2 || !fitsInLong) {
        } else if (value != Long.MIN_VALUE || negative) {
            this.peekedLong = negative ? value : -value;
            this.pos += i2;
            this.peeked = 15;
            return 15;
        } else {
            long j2 = value;
        }
        if (c2 != 2 && c2 != 4 && c2 != 7) {
            return 0;
        }
        this.peekedNumberLength = i2;
        this.peeked = 16;
        return 16;
    }

    private boolean isLiteral(char c) throws IOException {
        switch (c) {
            case 9:
            case 10:
            case 12:
            case 13:
            case ' ':
            case ',':
            case ':':
            case '[':
            case ']':
            case '{':
            case '}':
                break;
            case '#':
            case '/':
            case ';':
            case '=':
            case '\\':
                checkLenient();
                break;
            default:
                return true;
        }
        return false;
    }

    public String nextName() throws IOException {
        String result;
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 14) {
            result = nextUnquotedValue();
        } else if (p == 12) {
            result = nextQuotedValue('\'');
        } else if (p == 13) {
            result = nextQuotedValue('\"');
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Expected a name but was ");
            sb.append(peek());
            sb.append(" at line ");
            sb.append(getLineNumber());
            sb.append(" column ");
            sb.append(getColumnNumber());
            throw new IllegalStateException(sb.toString());
        }
        this.peeked = 0;
        return result;
    }

    public String nextString() throws IOException {
        String result;
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 10) {
            result = nextUnquotedValue();
        } else if (p == 8) {
            result = nextQuotedValue('\'');
        } else if (p == 9) {
            result = nextQuotedValue('\"');
        } else if (p == 11) {
            result = this.peekedString;
            this.peekedString = null;
        } else if (p == 15) {
            result = Long.toString(this.peekedLong);
        } else if (p == 16) {
            result = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Expected a string but was ");
            sb.append(peek());
            sb.append(" at line ");
            sb.append(getLineNumber());
            sb.append(" column ");
            sb.append(getColumnNumber());
            throw new IllegalStateException(sb.toString());
        }
        this.peeked = 0;
        return result;
    }

    public boolean nextBoolean() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 5) {
            this.peeked = 0;
            return true;
        } else if (p == 6) {
            this.peeked = 0;
            return false;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Expected a boolean but was ");
            sb.append(peek());
            sb.append(" at line ");
            sb.append(getLineNumber());
            sb.append(" column ");
            sb.append(getColumnNumber());
            throw new IllegalStateException(sb.toString());
        }
    }

    public void nextNull() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 7) {
            this.peeked = 0;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Expected null but was ");
        sb.append(peek());
        sb.append(" at line ");
        sb.append(getLineNumber());
        sb.append(" column ");
        sb.append(getColumnNumber());
        throw new IllegalStateException(sb.toString());
    }

    public double nextDouble() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 15) {
            this.peeked = 0;
            return (double) this.peekedLong;
        }
        if (p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (p == 8 || p == 9) {
            this.peekedString = nextQuotedValue(p == 8 ? '\'' : '\"');
        } else if (p == 10) {
            this.peekedString = nextUnquotedValue();
        } else if (p != 11) {
            StringBuilder sb = new StringBuilder();
            sb.append("Expected a double but was ");
            sb.append(peek());
            sb.append(" at line ");
            sb.append(getLineNumber());
            sb.append(" column ");
            sb.append(getColumnNumber());
            throw new IllegalStateException(sb.toString());
        }
        this.peeked = 11;
        double result = Double.parseDouble(this.peekedString);
        if (this.lenient || (!Double.isNaN(result) && !Double.isInfinite(result))) {
            this.peekedString = null;
            this.peeked = 0;
            return result;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("JSON forbids NaN and infinities: ");
        sb2.append(result);
        sb2.append(" at line ");
        sb2.append(getLineNumber());
        sb2.append(" column ");
        sb2.append(getColumnNumber());
        throw new MalformedJsonException(sb2.toString());
    }

    public long nextLong() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 15) {
            this.peeked = 0;
            return this.peekedLong;
        }
        if (p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (p == 8 || p == 9) {
            this.peekedString = nextQuotedValue(p == 8 ? '\'' : '\"');
            try {
                long result = Long.parseLong(this.peekedString);
                this.peeked = 0;
                return result;
            } catch (NumberFormatException e) {
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Expected a long but was ");
            sb.append(peek());
            sb.append(" at line ");
            sb.append(getLineNumber());
            sb.append(" column ");
            sb.append(getColumnNumber());
            throw new IllegalStateException(sb.toString());
        }
        this.peeked = 11;
        double asDouble = Double.parseDouble(this.peekedString);
        long result2 = (long) asDouble;
        if (((double) result2) == asDouble) {
            this.peekedString = null;
            this.peeked = 0;
            return result2;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Expected a long but was ");
        sb2.append(this.peekedString);
        sb2.append(" at line ");
        sb2.append(getLineNumber());
        sb2.append(" column ");
        sb2.append(getColumnNumber());
        throw new NumberFormatException(sb2.toString());
    }

    private String nextQuotedValue(char quote) throws IOException {
        char[] buffer2 = this.buffer;
        StringBuilder builder = new StringBuilder();
        while (true) {
            int start = this.pos;
            int l = this.limit;
            int c = start;
            while (c < l) {
                int p = c + 1;
                char c2 = buffer2[c];
                if (c2 == quote) {
                    this.pos = p;
                    builder.append(buffer2, start, (p - start) - 1);
                    return builder.toString();
                } else if (c2 == '\\') {
                    this.pos = p;
                    builder.append(buffer2, start, (p - start) - 1);
                    builder.append(readEscapeCharacter());
                    int p2 = this.pos;
                    l = this.limit;
                    start = p2;
                    c = p2;
                } else {
                    if (c2 == 10) {
                        this.lineNumber++;
                        this.lineStart = p;
                    }
                    c = p;
                }
            }
            builder.append(buffer2, start, c - start);
            this.pos = c;
            if (!fillBuffer(1)) {
                throw syntaxError("Unterminated string");
            }
        }
    }

    private String nextUnquotedValue() throws IOException {
        String result;
        StringBuilder builder = null;
        int i = 0;
        while (true) {
            if (this.pos + i < this.limit) {
                switch (this.buffer[this.pos + i]) {
                    case 9:
                    case 10:
                    case 12:
                    case 13:
                    case ' ':
                    case ',':
                    case ':':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                        break;
                    case '#':
                    case '/':
                    case ';':
                    case '=':
                    case '\\':
                        checkLenient();
                        break;
                    default:
                        i++;
                        continue;
                }
            } else if (i >= this.buffer.length) {
                if (builder == null) {
                    builder = new StringBuilder();
                }
                builder.append(this.buffer, this.pos, i);
                this.pos += i;
                i = 0;
                if (!fillBuffer(1)) {
                }
            } else if (fillBuffer(i + 1)) {
            }
        }
        if (builder == null) {
            result = new String(this.buffer, this.pos, i);
        } else {
            builder.append(this.buffer, this.pos, i);
            result = builder.toString();
        }
        this.pos += i;
        return result;
    }

    private void skipQuotedValue(char quote) throws IOException {
        char[] buffer2 = this.buffer;
        while (true) {
            int c = this.pos;
            int l = this.limit;
            while (c < l) {
                int p = c + 1;
                char c2 = buffer2[c];
                if (c2 == quote) {
                    this.pos = p;
                    return;
                } else if (c2 == '\\') {
                    this.pos = p;
                    readEscapeCharacter();
                    int p2 = this.pos;
                    l = this.limit;
                    c = p2;
                } else {
                    if (c2 == 10) {
                        this.lineNumber++;
                        this.lineStart = p;
                    }
                    c = p;
                }
            }
            this.pos = c;
            if (fillBuffer(1) == 0) {
                throw syntaxError("Unterminated string");
            }
        }
    }

    private void skipUnquotedValue() throws IOException {
        do {
            int i = 0;
            while (this.pos + i < this.limit) {
                switch (this.buffer[this.pos + i]) {
                    case 9:
                    case 10:
                    case 12:
                    case 13:
                    case ' ':
                    case ',':
                    case ':':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                        break;
                    case '#':
                    case '/':
                    case ';':
                    case '=':
                    case '\\':
                        checkLenient();
                        break;
                    default:
                        i++;
                }
                this.pos += i;
                return;
            }
            this.pos += i;
        } while (fillBuffer(1));
    }

    public int nextInt() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 15) {
            int result = (int) this.peekedLong;
            if (this.peekedLong == ((long) result)) {
                this.peeked = 0;
                return result;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Expected an int but was ");
            sb.append(this.peekedLong);
            sb.append(" at line ");
            sb.append(getLineNumber());
            sb.append(" column ");
            sb.append(getColumnNumber());
            throw new NumberFormatException(sb.toString());
        }
        if (p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (p == 8 || p == 9) {
            this.peekedString = nextQuotedValue(p == 8 ? '\'' : '\"');
            try {
                int result2 = Integer.parseInt(this.peekedString);
                try {
                    this.peeked = 0;
                    return result2;
                } catch (NumberFormatException e) {
                }
            } catch (NumberFormatException e2) {
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Expected an int but was ");
            sb2.append(peek());
            sb2.append(" at line ");
            sb2.append(getLineNumber());
            sb2.append(" column ");
            sb2.append(getColumnNumber());
            throw new IllegalStateException(sb2.toString());
        }
        this.peeked = 11;
        double asDouble = Double.parseDouble(this.peekedString);
        int result3 = (int) asDouble;
        if (((double) result3) == asDouble) {
            this.peekedString = null;
            this.peeked = 0;
            return result3;
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Expected an int but was ");
        sb3.append(this.peekedString);
        sb3.append(" at line ");
        sb3.append(getLineNumber());
        sb3.append(" column ");
        sb3.append(getColumnNumber());
        throw new NumberFormatException(sb3.toString());
    }

    public void close() throws IOException {
        this.peeked = 0;
        this.stack[0] = 8;
        this.stackSize = 1;
        this.in.close();
    }

    public void skipValue() throws IOException {
        int count = 0;
        do {
            int p = this.peeked;
            if (p == 0) {
                p = doPeek();
            }
            if (p == 3) {
                push(1);
                count++;
            } else if (p == 1) {
                push(3);
                count++;
            } else if (p == 4) {
                this.stackSize--;
                count--;
            } else if (p == 2) {
                this.stackSize--;
                count--;
            } else if (p == 14 || p == 10) {
                skipUnquotedValue();
            } else if (p == 8 || p == 12) {
                skipQuotedValue('\'');
            } else if (p == 9 || p == 13) {
                skipQuotedValue('\"');
            } else if (p == 16) {
                this.pos += this.peekedNumberLength;
            }
            this.peeked = 0;
        } while (count != 0);
    }

    private void push(int newTop) {
        if (this.stackSize == this.stack.length) {
            int[] newStack = new int[(this.stackSize * 2)];
            System.arraycopy(this.stack, 0, newStack, 0, this.stackSize);
            this.stack = newStack;
        }
        int[] newStack2 = this.stack;
        int i = this.stackSize;
        this.stackSize = i + 1;
        newStack2[i] = newTop;
    }

    private boolean fillBuffer(int minimum) throws IOException {
        char[] buffer2 = this.buffer;
        this.lineStart -= this.pos;
        if (this.limit != this.pos) {
            this.limit -= this.pos;
            System.arraycopy(buffer2, this.pos, buffer2, 0, this.limit);
        } else {
            this.limit = 0;
        }
        this.pos = 0;
        do {
            int read = this.in.read(buffer2, this.limit, buffer2.length - this.limit);
            int total = read;
            if (read == -1) {
                return false;
            }
            this.limit += total;
            if (this.lineNumber == 0 && this.lineStart == 0 && this.limit > 0 && buffer2[0] == 65279) {
                this.pos++;
                this.lineStart++;
                minimum++;
            }
        } while (this.limit < minimum);
        return true;
    }

    /* access modifiers changed from: private */
    public int getLineNumber() {
        return this.lineNumber + 1;
    }

    /* access modifiers changed from: private */
    public int getColumnNumber() {
        return (this.pos - this.lineStart) + 1;
    }

    private int nextNonWhitespace(boolean throwOnEof) throws IOException {
        int p;
        char[] buffer2 = this.buffer;
        int p2 = this.pos;
        int l = this.limit;
        while (true) {
            if (p2 == l) {
                this.pos = p2;
                if (fillBuffer(1)) {
                    p2 = this.pos;
                    l = this.limit;
                } else if (!throwOnEof) {
                    return -1;
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("End of input at line ");
                    sb.append(getLineNumber());
                    sb.append(" column ");
                    sb.append(getColumnNumber());
                    throw new EOFException(sb.toString());
                }
            }
            int p3 = p2 + 1;
            char c = buffer2[p2];
            if (c == 10) {
                this.lineNumber++;
                this.lineStart = p3;
            } else if (!(c == ' ' || c == 13 || c == 9)) {
                if (c == '/') {
                    this.pos = p3;
                    if (p3 == l) {
                        this.pos--;
                        boolean charsLoaded = fillBuffer(2);
                        this.pos++;
                        if (!charsLoaded) {
                            return c;
                        }
                    }
                    checkLenient();
                    char peek = buffer2[this.pos];
                    if (peek == '*') {
                        this.pos++;
                        if (skipTo("*/")) {
                            p = this.pos + 2;
                            l = this.limit;
                        } else {
                            throw syntaxError("Unterminated comment");
                        }
                    } else if (peek != '/') {
                        return c;
                    } else {
                        this.pos++;
                        skipToEndOfLine();
                        p = this.pos;
                        l = this.limit;
                    }
                } else if (c == '#') {
                    this.pos = p3;
                    checkLenient();
                    skipToEndOfLine();
                    p = this.pos;
                    l = this.limit;
                } else {
                    this.pos = p3;
                    return c;
                }
                p2 = p;
            }
            p2 = p3;
        }
    }

    private void checkLenient() throws IOException {
        if (!this.lenient) {
            throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void skipToEndOfLine() throws IOException {
        while (true) {
            if (this.pos < this.limit || fillBuffer(1)) {
                char[] cArr = this.buffer;
                int i = this.pos;
                this.pos = i + 1;
                char c = cArr[i];
                if (c == 10) {
                    this.lineNumber++;
                    this.lineStart = this.pos;
                    return;
                } else if (c == 13) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private boolean skipTo(String toFind) throws IOException {
        while (true) {
            int c = 0;
            if (this.pos + toFind.length() > this.limit && !fillBuffer(toFind.length())) {
                return false;
            }
            if (this.buffer[this.pos] == 10) {
                this.lineNumber++;
                this.lineStart = this.pos + 1;
            } else {
                while (true) {
                    int c2 = c;
                    if (c2 >= toFind.length()) {
                        return true;
                    }
                    if (this.buffer[this.pos + c2] != toFind.charAt(c2)) {
                        break;
                    }
                    c = c2 + 1;
                }
            }
            this.pos++;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" at line ");
        sb.append(getLineNumber());
        sb.append(" column ");
        sb.append(getColumnNumber());
        return sb.toString();
    }

    private char readEscapeCharacter() throws IOException {
        int i;
        if (this.pos != this.limit || fillBuffer(1)) {
            char[] cArr = this.buffer;
            int i2 = this.pos;
            this.pos = i2 + 1;
            char escaped = cArr[i2];
            if (escaped == 10) {
                this.lineNumber++;
                this.lineStart = this.pos;
            } else if (escaped == 'b') {
                return 8;
            } else {
                if (escaped == 'f') {
                    return 12;
                }
                if (escaped == 'n') {
                    return 10;
                }
                if (escaped == 'r') {
                    return 13;
                }
                switch (escaped) {
                    case 't':
                        return 9;
                    case 'u':
                        if (this.pos + 4 <= this.limit || fillBuffer(4)) {
                            char result = 0;
                            int i3 = this.pos;
                            int end = i3 + 4;
                            while (i3 < end) {
                                char c = this.buffer[i3];
                                char result2 = (char) (result << 4);
                                if (c >= '0' && c <= '9') {
                                    i = c - '0';
                                } else if (c >= 'a' && c <= 'f') {
                                    i = (c - 'a') + 10;
                                } else if (c < 'A' || c > 'F') {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("\\u");
                                    sb.append(new String(this.buffer, this.pos, 4));
                                    throw new NumberFormatException(sb.toString());
                                } else {
                                    i = (c - 'A') + 10;
                                }
                                result = (char) (i + result2);
                                i3++;
                            }
                            this.pos += 4;
                            return result;
                        }
                        throw syntaxError("Unterminated escape sequence");
                }
            }
            return escaped;
        }
        throw syntaxError("Unterminated escape sequence");
    }

    private IOException syntaxError(String message) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(message);
        sb.append(" at line ");
        sb.append(getLineNumber());
        sb.append(" column ");
        sb.append(getColumnNumber());
        throw new MalformedJsonException(sb.toString());
    }

    private void consumeNonExecutePrefix() throws IOException {
        nextNonWhitespace(true);
        this.pos--;
        if (this.pos + NON_EXECUTE_PREFIX.length <= this.limit || fillBuffer(NON_EXECUTE_PREFIX.length)) {
            int i = 0;
            while (i < NON_EXECUTE_PREFIX.length) {
                if (this.buffer[this.pos + i] == NON_EXECUTE_PREFIX[i]) {
                    i++;
                } else {
                    return;
                }
            }
            this.pos += NON_EXECUTE_PREFIX.length;
        }
    }
}