package org.robolectric.shadows;

import android.text.SpannedString;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;

@Implements(SpannedString.class)
public class ShadowSpannedString {

    private CharSequence charSequence;

    public void __constructor__(CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    @Override @Implementation
    public String toString() {
        return charSequence.toString();
    }

    @Implementation
    public static SpannedString valueOf(CharSequence charSequence) {
        if (charSequence instanceof SpannedString) {
            return (SpannedString) charSequence;
        }
        return new SpannedString(charSequence);
    }

}
