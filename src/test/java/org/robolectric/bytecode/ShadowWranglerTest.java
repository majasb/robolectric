package org.robolectric.bytecode;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.TestRunners;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;
import org.robolectric.internal.Instrument;
import org.robolectric.internal.RealObject;
import org.robolectric.util.I18nException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.robolectric.Robolectric.bindShadowClass;
import static org.robolectric.Robolectric.shadowOf_;

@RunWith(TestRunners.WithoutDefaults.class)
public class ShadowWranglerTest {
    private String name;

    @Before
    public void setUp() throws Exception {
        name = "context";
    }

    @Test
    public void testConstructorInvocation_WithDefaultConstructorAndNoConstructorDelegateOnShadowClass() throws Exception {
        bindShadowClass(ShadowFoo_WithDefaultConstructorAndNoConstructorDelegate.class);

        Foo foo = new Foo(name);
        assertEquals(ShadowFoo_WithDefaultConstructorAndNoConstructorDelegate.class, shadowOf_(foo).getClass());
    }

    @Test
    public void testConstructorInvocation() throws Exception {
        bindShadowClass(ShadowFoo.class);

        Foo foo = new Foo(name);
        assertSame(name, shadowOf(foo).name);
        assertSame(foo, shadowOf(foo).realFooCtor);
    }

    @Test
    public void testRealObjectAnnotatedFieldsAreSetBeforeConstructorIsCalled() throws Exception {
        bindShadowClass(ShadowFoo.class);

        Foo foo = new Foo(name);
        assertSame(name, shadowOf(foo).name);
        assertSame(foo, shadowOf(foo).realFooField);

        assertSame(foo, shadowOf(foo).realFooInConstructor);
        assertSame(foo, shadowOf(foo).realFooInParentConstructor);
    }

    @Test
    public void testMethodDelegation() throws Exception {
        bindShadowClass(ShadowFoo.class);

        Foo foo = new Foo(name);
        assertSame(name, foo.getName());
    }

    @Test
    public void testEqualsMethodDelegation() throws Exception {
        bindShadowClass(WithEquals.class);

        Foo foo1 = new Foo(name);
        Foo foo2 = new Foo(name);
        assertEquals(foo1, foo2);
    }

    @Test
    public void testHashCodeMethodDelegation() throws Exception {
        bindShadowClass(WithEquals.class);

        Foo foo = new Foo(name);
        assertEquals(42, foo.hashCode());
    }

    @Test
    public void testToStringMethodDelegation() throws Exception {
        bindShadowClass(WithToString.class);

        Foo foo = new Foo(name);
        assertEquals("the expected string", foo.toString());
    }

    @Test
    public void testShadowSelectionSearchesSuperclasses() throws Exception {
        bindShadowClass(ShadowFoo.class);

        TextFoo textFoo = new TextFoo(name);
        assertEquals(ShadowFoo.class, shadowOf_(textFoo).getClass());
    }

    @Test
    public void shouldUseMostSpecificShadow() throws Exception {
        bindShadowClass(ShadowFoo.class);
        bindShadowClass(ShadowTextFoo.class);

        TextFoo textFoo = new TextFoo(name);
        assertThat(shadowOf(textFoo)).isInstanceOf(ShadowTextFoo.class);
    }

    @Test
    public void testPrimitiveArrays() throws Exception {
        Class<?> objArrayClass = ShadowWrangler.loadClass("java.lang.Object[]", getClass().getClassLoader());
        assertTrue(objArrayClass.isArray());
        assertEquals(Object.class, objArrayClass.getComponentType());

        Class<?> intArrayClass = ShadowWrangler.loadClass("int[]", getClass().getClassLoader());
        assertTrue(intArrayClass.isArray());
        assertEquals(Integer.TYPE, intArrayClass.getComponentType());
    }

    @Test
    public void shouldRemoveNoiseFromStackTraces() throws Exception {
        bindShadowClass(ExceptionThrowingShadowFoo.class);
        Foo foo = new Foo(null);

        Exception e = null;
        try {
            foo.getName();
        } catch (Exception e1) {
            e = e1;
        }

        assertNotNull(e);
        assertEquals(IOException.class, e.getClass());
        assertEquals("fake exception", e.getMessage());
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String stackTrace = stringWriter.getBuffer().toString();

        assertThat(stackTrace).contains("fake exception");
        assertThat(stackTrace).contains(ExceptionThrowingShadowFoo.class.getName() + ".getName(");
        assertThat(stackTrace).contains(Foo.class.getName() + ".getName(");
        assertThat(stackTrace).contains(ShadowWranglerTest.class.getName() + ".shouldRemoveNoiseFromStackTraces");

        assertThat(stackTrace).doesNotContain("sun.reflect");
        assertThat(stackTrace).doesNotContain("java.lang.reflect");
        assertThat(stackTrace).doesNotContain(ShadowWrangler.class.getName() + ".");
        assertThat(stackTrace).doesNotContain(RobolectricInternals.class.getName() + ".");
    }

    @Test(expected = I18nException.class)
    public void shouldThrowExceptionOnI18nStrictMode() {
        Robolectric.getShadowWrangler().setStrictI18n(true);
        bindShadowClass(ShadowFooI18n.class);
        Foo foo = new Foo(null);
        foo.getName();
    }

    private ShadowFoo shadowOf(Foo foo) {
        return (ShadowFoo) shadowOf_(foo);
    }

    private ShadowTextFoo shadowOf(TextFoo foo) {
        return (ShadowTextFoo) shadowOf_(foo);
    }

    @Implements(Foo.class)
    public static class WithEquals {
        @Override
        public boolean equals(Object o) {
            return true;
        }


        @Override
        public int hashCode() {
            return 42;
        }

    }

    @Implements(Foo.class)
    public static class WithToString {
        @Override
        public String toString() {
            return "the expected string";
        }
    }

    @Implements(TextFoo.class)
    public static class ShadowTextFoo {
    }

    @Instrument
    public static class TextFoo extends Foo {
        public TextFoo(String s) {
            super(s);
        }
    }
    
    @Implements(Foo.class)
    public static class ShadowFooI18n {
    	String name;

        public void __constructor__(String name) {
           this.name = name;
        }

    	@Implementation(i18nSafe=false)
    	public String getName() {
    		return name;
    	}
    }

    @Implements(Foo.class)
    public static class ShadowFooParent {
        @RealObject
        private Foo realFoo;
        Foo realFooInParentConstructor;

        public void __constructor__(String name) {
            realFooInParentConstructor = realFoo;
        }
    }

    @Implements(Foo.class)
    public static class ShadowFoo_WithDefaultConstructorAndNoConstructorDelegate {
    }

    @Implements(Foo.class)
    public static class ExceptionThrowingShadowFoo {
        @SuppressWarnings({"UnusedDeclaration"})
        public String getName() throws IOException {
            throw new IOException("fake exception");
        }
    }
}
