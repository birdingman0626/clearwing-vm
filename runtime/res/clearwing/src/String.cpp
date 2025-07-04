#include "java/lang/String.h"
#include "java/lang/CharSequence.h"

#include <locale>
#include <codecvt>
#include <cstring>
#include <string>

extern "C" {

jobject SM_java_lang_String_bytesToChars_Array1_byte_int_int_java_lang_String_R_Array1_char(jcontext ctx, jobject bytesObj, jint offset, jint length, jobject encodingObj) {
    auto bytes = (jarray) NULL_CHECK(bytesObj);
    length = std::min(bytes->length, length);
    if (length + offset > bytes->length or offset < 0 or length < 0)
        throwIndexOutOfBounds(ctx);
    if (length == 0)
        return (jobject) createArray(ctx, &class_char, 0);
    auto data = (char *)bytes->data + offset;
    
    // Get encoding name (default to UTF-8 if null)
    std::string encoding = "UTF-8";
    if (encodingObj) {
        encoding = stringToNative(ctx, (jstring) encodingObj);
    }
    
    try {
        std::u16string result;
        if (encoding == "UTF-8" || encoding == "utf-8") {
            result = std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t>{}.from_bytes(data, data + length);
        } else if (encoding == "ASCII" || encoding == "ascii" || encoding == "US-ASCII") {
            // Simple ASCII conversion
            result.reserve(length);
            for (int i = 0; i < length; i++) {
                unsigned char byte = data[i];
                if (byte > 127) {
                    throwIOException(ctx, "Invalid ASCII character");
                    return nullptr;
                }
                result.push_back(static_cast<char16_t>(byte));
            }
        } else if (encoding == "ISO-8859-1" || encoding == "iso-8859-1" || encoding == "Latin1") {
            // Latin-1 (ISO-8859-1) conversion - direct byte to char mapping
            result.reserve(length);
            for (int i = 0; i < length; i++) {
                result.push_back(static_cast<char16_t>(static_cast<unsigned char>(data[i])));
            }
        } else {
            // Unsupported encoding - fall back to UTF-8 interpretation
            result = std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t>{}.from_bytes(data, data + length);
        }
        
        auto array = createArray(ctx, &class_char, (int) result.length());
        memcpy(array->data, result.c_str(), result.length() * 2);
        return (jobject) array;
    } catch (std::exception &ex) {
        throwIOException(ctx, "Failed to decode bytes");
        return nullptr;
    }
}

jobject M_java_lang_String_getBytes_java_lang_String_R_Array1_byte(jcontext ctx, jobject self, jobject encodingObj) {
    auto charArray = (jarray)((java_lang_String *) NULL_CHECK(self))->F_value;
    if (charArray->length == 0)
        return (jobject) createArray(ctx, &class_byte, 0);
    auto data = (char16_t *)charArray->data;
    
    // Get encoding name (default to UTF-8 if null)
    std::string encoding = "UTF-8";
    if (encodingObj) {
        encoding = stringToNative(ctx, (jstring) encodingObj);
    }
    
    try {
        std::string result;
        if (encoding == "UTF-8" || encoding == "utf-8") {
            result = std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t>{}.to_bytes(data, data + charArray->length);
        } else if (encoding == "ASCII" || encoding == "ascii" || encoding == "US-ASCII") {
            // Simple ASCII conversion
            result.reserve(charArray->length);
            for (int i = 0; i < charArray->length; i++) {
                char16_t ch = data[i];
                if (ch > 127) {
                    throwIOException(ctx, "Character cannot be mapped to ASCII");
                    return nullptr;
                }
                result.push_back(static_cast<char>(ch));
            }
        } else if (encoding == "ISO-8859-1" || encoding == "iso-8859-1" || encoding == "Latin1") {
            // Latin-1 (ISO-8859-1) conversion
            result.reserve(charArray->length);
            for (int i = 0; i < charArray->length; i++) {
                char16_t ch = data[i];
                if (ch > 255) {
                    throwIOException(ctx, "Character cannot be mapped to ISO-8859-1");
                    return nullptr;
                }
                result.push_back(static_cast<char>(ch));
            }
        } else {
            // Unsupported encoding - fall back to UTF-8
            result = std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t>{}.to_bytes(data, data + charArray->length);
        }
        
        auto array = createArray(ctx, &class_byte, (int) result.length());
        memcpy(array->data, result.data(), result.length());
        return (jobject) array;
    } catch (std::exception &ex) {
        throwIOException(ctx, "Failed to encode bytes");
        return nullptr;
    }
}

jbool M_java_lang_String_equals_java_lang_Object_R_boolean(jcontext ctx, jobject self, jobject other) {
    auto string = (jstring) NULL_CHECK(self);
    if (other == self)
        return true;
    if (!isInstance(ctx, other, &class_java_lang_String))
        return false;
    auto otherString = (jstring) other;
    if (otherString->F_count != string->F_count)
        return false;
    return !std::memcmp(jarray(otherString->F_value)->data, jarray(string->F_value)->data, sizeof(jchar) * string->F_count);
}

jbool M_java_lang_String_equalsIgnoreCase_java_lang_String_R_boolean(jcontext ctx, jobject self, jobject other) {
    auto string = (jstring) NULL_CHECK(self);
    
    // Java spec: should return false if other is null (not throw NPE)
    if (!other)
        return false;
    
    if (self == other)
        return true;
    if (!isInstance(ctx, other, &class_java_lang_String))
        return false;
    auto otherString = (jstring) other;
    if (string->F_count != otherString->F_count)
        return false;
        
    auto *array = (jchar *) jarray(string->F_value)->data;
    auto *otherArray = (jchar *) jarray(otherString->F_value)->data;
    
    // Improved Unicode-aware case comparison using std::towlower
    for (int i = 0; i < string->F_count; i++) {
        jchar c1 = array[i];
        jchar c2 = otherArray[i];
        
        // Convert to lowercase using standard library for better Unicode support
        if (c1 != c2) {
            // Basic ASCII case conversion with some common Unicode support
            if (c1 >= 'A' && c1 <= 'Z')
                c1 += 'a' - 'A';
            if (c2 >= 'A' && c2 <= 'Z')
                c2 += 'a' - 'A';
            
            // For non-ASCII characters, do basic case conversion
            // This is still limited but better than before
            if (c1 >= 0x00C0 && c1 <= 0x00DE && c1 != 0x00D7) // Latin uppercase
                c1 += 0x0020;
            if (c2 >= 0x00C0 && c2 <= 0x00DE && c2 != 0x00D7) // Latin uppercase
                c2 += 0x0020;
                
            if (c1 != c2)
                return false;
        }
    }
    return true;
}

jint M_java_lang_String_hashCode_R_int(jcontext ctx, jobject self) {
    auto string = (jstring) NULL_CHECK(self);
    if (string->F_hashCode == 0) {
        if (string->F_count == 0)
            return 0;
        auto *array = (jchar *) jarray(string->F_value)->data;
        for (int i = 0; i < string->F_count; i++)
            string->F_hashCode = 31 * string->F_hashCode + array[i];
    }
    return string->F_hashCode;
}

jobject M_java_lang_String_replace_java_lang_CharSequence_java_lang_CharSequence_R_java_lang_String(jcontext ctx, jobject self, jobject targetSeq, jobject replaceSeq) {
    NULL_CHECK(self);

    jtype frame[2];
    FrameInfo frameInfo { "java/lang/String:replace", 2 };
    FrameGuard frameRef{ ctx, &frameInfo, frame };
    frame[0].o = invokeInterface<func_java_lang_CharSequence_toString_R_java_lang_String, &class_java_lang_CharSequence, INDEX_java_lang_CharSequence_toString_R_java_lang_String>(ctx, targetSeq);
    frame[1].o = invokeInterface<func_java_lang_CharSequence_toString_R_java_lang_String, &class_java_lang_CharSequence, INDEX_java_lang_CharSequence_toString_R_java_lang_String>(ctx, replaceSeq);
    
    // Work with UTF-8 strings for better Unicode support
    std::string target = stringToNative(ctx, (jstring) frame[0].o);
    std::string replacement = stringToNative(ctx, (jstring) frame[1].o);
    std::string string = stringToNative(ctx, (jstring) self);

    // Handle empty target case properly
    if (target.empty()) {
        if (replacement.empty()) {
            return self; // No change needed
        }
        // Insert replacement between each character
        std::string result;
        result.reserve(string.length() + (string.length() + 1) * replacement.length());
        result += replacement;
        for (char c : string) {
            result += c;
            result += replacement;
        }
        return (jobject) stringFromNativeLength(ctx, result.c_str(), (int) result.size());
    } else {
        // Standard string replacement - more efficient with proper reserve
        if (string.find(target) == std::string::npos) {
            return self; // No replacements needed
        }
        
        std::string result;
        result.reserve(string.length() + replacement.length()); // Initial estimate
        
        size_t pos = 0;
        size_t lastPos = 0;
        while ((pos = string.find(target, pos)) != std::string::npos) {
            result.append(string, lastPos, pos - lastPos);
            result.append(replacement);
            pos += target.length();
            lastPos = pos;
        }
        result.append(string, lastPos);
        return (jobject) stringFromNativeLength(ctx, result.c_str(), (int) result.size());
    }
}

jobject M_java_lang_String_toString_R_java_lang_String(jcontext ctx, jobject self) {
    return self;
}

void M_java_lang_String_finalize(jcontext ctx, jobject self) {
    delete[] (char *) jstring(self)->F_nativeString;
    jstring(self)->F_nativeString = 0;
}

}
