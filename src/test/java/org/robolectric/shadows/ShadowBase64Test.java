package org.robolectric.shadows;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static android.util.Base64.DEFAULT;
import static org.fest.assertions.api.Assertions.assertThat;

public class ShadowBase64Test {

    @Test
    public void testEncode() throws UnsupportedEncodingException {
        String inputString = "Some nice String";
        String encodedString = ShadowBase64.encodeToString(inputString.getBytes("UTF-8"), DEFAULT);
        assertThat(encodedString).isEqualTo("U29tZSBuaWNlIFN0cmluZw==\n");
    }

    @Test
    public void testDecode() throws Exception {
        byte[] decodedBytes = ShadowBase64.decode("U29tZSBuaWNlIFN0cmluZw==", DEFAULT);
        assertThat(new String(decodedBytes)).isEqualTo("Some nice String");
    }
}
