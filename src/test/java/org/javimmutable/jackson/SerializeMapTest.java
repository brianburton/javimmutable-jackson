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

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

public class SerializeMapTest
    extends TestCase
{
    public void test()
        throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JImmutableModule());

        JImmutableMap<String, Inner> innerMap = JImmutables.map();
        JImmutableMap<String, Integer> intMap = JImmutables.map();
        Outer root = new Outer("a",
                                     innerMap.assign("b", new Inner(intMap.assign("t", 1).assign("e", 2))),
                                     innerMap.assign("c", new Inner(intMap.assign("w", 1))));
        String json = mapper.writeValueAsString(root);
        assertEquals("{\"something\":\"a\",\"alpha\":{\"b\":{\"gamma\":{\"e\":2,\"t\":1}}},\"beta\":{\"c\":{\"gamma\":{\"w\":1}}}}", json);
        assertEquals(root, mapper.readValue(json, Outer.class));
    }

    @Immutable
    public static class Outer
    {
        private final String something;
        private final JImmutableMap<String, Inner> alpha;
        private final JImmutableMap<String, Inner> beta;

        @JsonCreator
        public Outer(@JsonProperty("something") String something,
                     @JsonProperty("alpha") JImmutableMap<String, Inner> alpha,
                     @JsonProperty("beta") JImmutableMap<String, Inner> beta)
        {
            this.something = something;
            this.alpha = alpha;
            this.beta = beta;
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
            return beta != null ? beta.equals(outer.beta) : outer.beta == null;
        }

        @Override
        public int hashCode()
        {
            int result = something != null ? something.hashCode() : 0;
            result = 31 * result + (alpha != null ? alpha.hashCode() : 0);
            result = 31 * result + (beta != null ? beta.hashCode() : 0);
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
}
