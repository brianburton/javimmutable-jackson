///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.util.JImmutables;
import org.javimmutable.jackson.orderings.InsertOrderSet;
import org.javimmutable.jackson.orderings.JsonJImmutableSorted;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.javimmutable.collections.util.JImmutables.*;

public class SerializeSetTest
    extends TestCase
{

    private static final Pattern VALUE_STRING_REGEX = Pattern.compile("\\{\"(\\w+)\":\\[(-?\\d+(,-?\\d+)*)\\]\\}");

    public void testSet()
        throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JImmutableModule());
        SetBean bean = new SetBean();
        bean.setValues(JImmutables.<Integer>set().insert(1000).insert(-2000).insert(3000));
        String json = mapper.writeValueAsString(bean);
        assertEquals("{\"values\":[-2000,1000,3000]}", sortValues(json));
        assertEquals(bean, mapper.readValue(json, bean.getClass()));
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, bean.getClass())));

        bean.setValues(JImmutables.<Integer>set());
        json = mapper.writeValueAsString(bean);
        assertEquals("{\"values\":[]}", json);
        assertEquals(bean, mapper.readValue(json, bean.getClass()));

        bean.setValues(JImmutables.<Integer>set().insert(2500));
        json = mapper.writeValueAsString(bean);
        assertEquals("{\"values\":[2500]}", json);
        assertEquals(bean, mapper.readValue(json, bean.getClass()));
    }

    public void testConstructorSet()
        throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JImmutableModule());
        JImmutableSet<Integer> values = set();
        ConstructorSetBean bean = new ConstructorSetBean(values.insert(1000).insert(-2000).insert(3000));
        String json = mapper.writeValueAsString(bean);
        assertEquals("{\"values\":[-2000,1000,3000]}", sortValues(json));
        assertEquals(bean, mapper.readValue(json, bean.getClass()));
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, bean.getClass())));

        bean = new ConstructorSetBean(JImmutables.<Integer>set());
        json = mapper.writeValueAsString(bean);
        assertEquals("{\"values\":[]}", json);
        assertEquals(bean, mapper.readValue(json, bean.getClass()));

        bean = new ConstructorSetBean(JImmutables.<Integer>set().insert(2500));
        json = mapper.writeValueAsString(bean);
        assertEquals("{\"values\":[2500]}", json);
        assertEquals(bean, mapper.readValue(json, bean.getClass()));
    }

    private String sortValues(@Nonnull String valuesString)
    {
        Matcher m = VALUE_STRING_REGEX.matcher(valuesString);
        if (!m.matches()) {
            return valuesString;
        }
        final String field = m.group(1);
        final String[] numbers = m.group(2).split(",");
        final String sorted = Stream.of(numbers)
            .map(Integer::parseInt)
            .sorted()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
        return String.format("{\"%s\":[%s]}", field, sorted);
    }

    public void testSortedSet()
        throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JImmutableModule());
        JImmutableSet<Integer> sorted = JImmutables.<Integer>sortedSet().insert(1000).insert(-2000).insert(3000);
        SetBean bean = new SetBean();
        bean.setSorted(sorted);
        String json = mapper.writeValueAsString(bean);
        assertEquals("{\"sorted\":[-2000,1000,3000]}", json);
        assertEquals(bean, mapper.readValue(json, bean.getClass()));
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, bean.getClass())));

        bean = new SetBean();
        sorted = JImmutables.sortedSet();
        bean.setSorted(sorted);
        json = mapper.writeValueAsString(bean);
        assertEquals("{\"sorted\":[]}", json);
        assertEquals(bean, mapper.readValue(json, bean.getClass()));

        sorted = JImmutables.<Integer>sortedSet().insert(2500);
        bean = new SetBean();
        bean.setSorted(sorted);
        json = mapper.writeValueAsString(bean);
        assertEquals("{\"sorted\":[2500]}", json);
        assertEquals(bean, mapper.readValue(json, bean.getClass()));
    }

    public void testInsertOrderSet()
        throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JImmutableModule());
        JImmutableSet<Integer> inorder = JImmutables.<Integer>insertOrderSet().insert(1000).insert(-2000).insert(3000);
        SetBean bean = new SetBean();
        bean.setInorder(inorder);
        String json = mapper.writeValueAsString(bean);
        assertEquals("{\"inorder\":[1000,-2000,3000]}", json);
        assertEquals(bean, mapper.readValue(json, bean.getClass()));
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, bean.getClass())));

        bean = new SetBean();
        inorder = JImmutables.insertOrderSet();
        bean.setInorder(inorder);
        json = mapper.writeValueAsString(bean);
        assertEquals("{\"inorder\":[]}", json);
        assertEquals(bean, mapper.readValue(json, bean.getClass()));

        inorder = JImmutables.<Integer>insertOrderSet().insert(2500);
        bean = new SetBean();
        bean.setInorder(inorder);
        json = mapper.writeValueAsString(bean);
        assertEquals("{\"inorder\":[2500]}", json);
        assertEquals(bean, mapper.readValue(json, bean.getClass()));
    }

    public void testUnsortableSet()
        throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JImmutableModule());
        JImmutableSet<Object> inorder = JImmutables.insertOrderSet().insert(1000).insert(-2000).insert(3000);
        UnsortableSetBean bean = new UnsortableSetBean();
        bean.setSorted(inorder);
        String json = mapper.writeValueAsString(bean);
        assertEquals("{\"sorted\":[1000,-2000,3000]}", json);
        try {
            assertEquals(bean, mapper.readValue(json, bean.getClass()));
            fail();
        } catch (JsonMappingException ex) {
            // success
        }
    }

    public void testUntyped()
        throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JImmutableModule());
        final JImmutableSet<Object> values = JImmutables.<Object>insertOrderSet(new ArrayList<Object>(Arrays.asList(18, 76)), "a", 32);
        UntypedSetBean bean = new UntypedSetBean(values);
        String json = mapper.writeValueAsString(bean);
        assertEquals("{\"objects\":[[18,76],\"a\",32]}", json);
        assertEquals(bean, mapper.readValue(json, bean.getClass()));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SetBean
    {
        private JImmutableSet<Integer> values;
        @JsonJImmutableSorted
        private JImmutableSet<Integer> sorted;
        @JsonDeserialize(as = InsertOrderSet.class)
        private JImmutableSet<Integer> inorder;

        public JImmutableSet<Integer> getValues()
        {
            return values;
        }

        public void setValues(JImmutableSet<Integer> values)
        {
            this.values = values;
        }

        public JImmutableSet<Integer> getSorted()
        {
            return sorted;
        }

        public void setSorted(JImmutableSet<Integer> sorted)
        {
            this.sorted = sorted;
        }

        public JImmutableSet<Integer> getInorder()
        {
            return inorder;
        }

        public void setInorder(JImmutableSet<Integer> inorder)
        {
            this.inorder = inorder;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SetBean setBean = (SetBean)o;

            if (values != null ? !values.equals(setBean.values) : setBean.values != null) {
                return false;
            }
            if (sorted != null ? !sorted.equals(setBean.sorted) : setBean.sorted != null) {
                return false;
            }
            return !(inorder != null ? !inorder.equals(setBean.inorder) : setBean.inorder != null);

        }

        @Override
        public int hashCode()
        {
            int result = values != null ? values.hashCode() : 0;
            result = 31 * result + (sorted != null ? sorted.hashCode() : 0);
            result = 31 * result + (inorder != null ? inorder.hashCode() : 0);
            return result;
        }
    }

    public static class ConstructorSetBean
    {
        private final JImmutableSet<Integer> values;

        public ConstructorSetBean(@JsonProperty("values") JImmutableSet<Integer> values)
        {
            this.values = values;
        }

        public JImmutableSet<Integer> getValues()
        {
            return values;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ConstructorSetBean listBean = (ConstructorSetBean)o;

            return values.equals(listBean.values);

        }

        @Override
        public int hashCode()
        {
            return values.hashCode();
        }
    }

    public static class UnsortableSetBean
    {
        @JsonJImmutableSorted
        private JImmutableSet<Object> sorted;

        public JImmutableSet<Object> getSorted()
        {
            return sorted;
        }

        public void setSorted(JImmutableSet<Object> sorted)
        {
            this.sorted = sorted;
        }
    }

    public static class UntypedSetBean
    {
        @JsonDeserialize(as = InsertOrderSet.class)
        private final JImmutableSet<Object> objects;

        public UntypedSetBean(@JsonProperty("objects") JImmutableSet<Object> objects)
        {
            this.objects = objects;
        }

        public JImmutableSet<Object> getObjects()
        {
            return objects;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            UntypedSetBean that = (UntypedSetBean)o;
            return Objects.equals(objects, that.objects);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(objects);
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder("[");
            for (Object object : objects) {
                if (sb.length() > 1) {
                    sb.append(",");
                }
                sb.append(object.getClass().getSimpleName());
                sb.append("=");
                sb.append(object.toString());
            }
            sb.append("]");
            return sb.toString();
        }
    }
}
