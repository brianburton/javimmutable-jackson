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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.util.JImmutables;
import org.javimmutable.jackson.orderings.JsonJImmutableInsertOrder;
import org.javimmutable.jackson.orderings.JsonJImmutableSorted;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

import static org.javimmutable.collections.util.JImmutables.*;

public class SerializeMapTest
    extends TestCase
{

    private ObjectMapper mapper;

    @Override
    public void setUp()
        throws Exception
    {
        mapper = new ObjectMapper();
        mapper.registerModules(new JImmutableModule());
    }

    public void test()
        throws Exception
    {
        JImmutableMap<String, Inner> innerMap = JImmutables.map();
        JImmutableMap<String, Integer> intMap = JImmutables.map();
        JImmutableMap<String, Integer> sortedMap = JImmutables.sortedMap();
        Outer root = new Outer("a",
                               innerMap.assign("b", new Inner(intMap.assign("t", 1).assign("e", 2))),
                               innerMap.assign("c", new Inner(intMap.assign("w", 1))),
                               new Sorted(sortedMap.assign("x", 20).assign("a", -203).assign("q", 7563)));
        String json = mapper.writeValueAsString(root);
        assertEquals("{\"something\":\"a\",\"alpha\":{\"b\":{\"gamma\":{\"e\":2,\"t\":1}}},\"beta\":{\"c\":{\"gamma\":{\"w\":1}}},\"sorted\":{\"sortMe\":{\"a\":-203,\"q\":7563,\"x\":20}}}", json);
        assertEquals(root, mapper.readValue(json, Outer.class));

        JImmutableMap<String, Integer> insertOrderMap = JImmutables.insertOrderMap();
        InsertOrder io = new InsertOrder(insertOrderMap.assign("x", 1).assign("a", 2));
        json = mapper.writeValueAsString(io);
        assertEquals("{\"objects\":{\"x\":1,\"a\":2}}", json);
        assertEquals(io, mapper.readValue(json, InsertOrder.class));
    }

    public void testAnnotatedConstructor()
        throws Exception
    {
        final String json = "{\n" +
                            "  \"sorted\":{\n" +
                            "    \"i\":9,\n" +
                            "    \"a\":1,\n" +
                            "    \"d\":4,\n" +
                            "    \"g\":7\n" +
                            "  },\n" +
                            "  \"inorder\":{\n" +
                            "    \"i\":9,\n" +
                            "    \"a\":1,\n" +
                            "    \"d\":4,\n" +
                            "    \"g\":7\n" +
                            "  }\n" +
                            "}";
        AnnotatedConstructorBean bean = mapper.readValue(json, AnnotatedConstructorBean.class);
        assertEquals(list("a", "d", "g", "i"), list(bean.getSorted().keys()));
        assertEquals(list("i", "a", "d", "g"), list(bean.getInorder().keys()));
    }

    @Immutable
    public static class Outer
    {
        private final String something;
        private final JImmutableMap<String, Inner> alpha;
        private final JImmutableMap<String, Inner> beta;
        private final Sorted sorted;

        @JsonCreator
        public Outer(@JsonProperty("something") String something,
                     @JsonProperty("alpha") JImmutableMap<String, Inner> alpha,
                     @JsonProperty("beta") JImmutableMap<String, Inner> beta,
                     @JsonProperty("sorted") Sorted sorted)
        {
            this.something = something;
            this.alpha = alpha;
            this.beta = beta;
            this.sorted = sorted;
        }

        public String getSomething()
        {
            return something;
        }

        public JImmutableMap<String, Inner> getAlpha()
        {
            return alpha;
        }

        public JImmutableMap<String, Inner> getBeta()
        {
            return beta;
        }

        public Sorted getSorted()
        {
            return sorted;
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

            Outer outer = (Outer)o;

            if (something != null ? !something.equals(outer.something) : outer.something != null) {
                return false;
            }
            if (alpha != null ? !alpha.equals(outer.alpha) : outer.alpha != null) {
                return false;
            }
            if (beta != null ? !beta.equals(outer.beta) : outer.beta != null) {
                return false;
            }
            return sorted != null ? sorted.equals(outer.sorted) : outer.sorted == null;
        }

        @Override
        public int hashCode()
        {
            int result = something != null ? something.hashCode() : 0;
            result = 31 * result + (alpha != null ? alpha.hashCode() : 0);
            result = 31 * result + (beta != null ? beta.hashCode() : 0);
            result = 31 * result + (sorted != null ? sorted.hashCode() : 0);
            return result;
        }
    }

    @Immutable
    public static class Inner
    {
        private final JImmutableMap<String, Integer> gamma;

        @JsonCreator
        public Inner(@JsonProperty("gamma") JImmutableMap<String, Integer> gamma)
        {
            this.gamma = gamma;
        }

        public JImmutableMap<String, Integer> getGamma()
        {
            return gamma;
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
            Inner inner = (Inner)o;
            return Objects.equals(gamma, inner.gamma);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(gamma);
        }
    }

    @Immutable
    public static class Sorted
    {
        @JsonJImmutableSorted
        private final JImmutableMap<String, Integer> sortMe;

        @JsonCreator
        public Sorted(@JsonProperty("sortMe") JImmutableMap<String, Integer> sortMe)
        {
            assertEquals(JImmutables.sortedMap().getClass(), sortMe.getClass());
            this.sortMe = sortMe;
        }

        public JImmutableMap<String, Integer> getSortMe()
        {
            return sortMe;
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
            Sorted inner = (Sorted)o;
            return Objects.equals(sortMe, inner.sortMe);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(sortMe);
        }
    }

    public static class InsertOrder
    {
        @JsonJImmutableInsertOrder
        private final JImmutableMap<String, Integer> objects;

        @JsonCreator
        public InsertOrder(@JsonProperty("objects") JImmutableMap<String, Integer> objects)
        {
            assertEquals(JImmutables.insertOrderMap().getClass(), objects.getClass());
            this.objects = objects;
        }

        public JImmutableMap<String, Integer> getObjects()
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

            InsertOrder that = (InsertOrder)o;

            return objects != null ? objects.equals(that.objects) : that.objects == null;
        }

        @Override
        public int hashCode()
        {
            return objects != null ? objects.hashCode() : 0;
        }
    }

    public static class AnnotatedConstructorBean
    {
        private final JImmutableMap<String, Integer> sorted;
        private final JImmutableMap<String, Integer> inorder;

        public AnnotatedConstructorBean(@JsonProperty("sorted") @JsonJImmutableSorted JImmutableMap<String, Integer> sorted,
                                        @JsonProperty("inorder") @JsonJImmutableInsertOrder JImmutableMap<String, Integer> inorder)
        {
            this.sorted = sorted;
            this.inorder = inorder;
        }

        public JImmutableMap<String, Integer> getSorted()
        {
            return sorted;
        }

        public JImmutableMap<String, Integer> getInorder()
        {
            return inorder;
        }
    }
}
