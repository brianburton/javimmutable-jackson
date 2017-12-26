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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableRandomAccessList;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

import static org.javimmutable.collections.util.JImmutables.ralist;

public class SerializeRandomAccessListTest
    extends TestCase
{
    public void test()
        throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JImmutableModule());

        final NamesBean names = new NamesBean(ralist("jones", "smith"));
        String json = mapper.writeValueAsString(names);
        assertEquals("{\"names\":[\"jones\",\"smith\"]}", json);
        assertEquals(names, mapper.readValue(json, NamesBean.class));

        final OrgBean org = new OrgBean(names, ralist("oxford", "cambridge"));
        json = mapper.writeValueAsString(org);
        assertEquals("{\"names\":{\"names\":[\"jones\",\"smith\"]},\"cities\":[\"oxford\",\"cambridge\"]}", json);
        assertEquals(org, mapper.readValue(json, OrgBean.class));
    }

    @Immutable
    public static class OrgBean
    {
        private final NamesBean names;
        private final JImmutableRandomAccessList<String> cities;

        public OrgBean(@JsonProperty("names") NamesBean names,
                       @JsonProperty("cities") JImmutableRandomAccessList<String> cities)
        {
            this.names = names;
            this.cities = cities;
        }

        public NamesBean getNames()
        {
            return names;
        }

        public JImmutableRandomAccessList<String> getCities()
        {
            return cities;
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
            OrgBean orgBean = (OrgBean)o;
            return Objects.equals(names, orgBean.names) &&
                   Objects.equals(cities, orgBean.cities);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(names, cities);
        }
    }

    @Immutable
    public static class NamesBean
    {
        private final JImmutableRandomAccessList<String> names;

        public NamesBean(@JsonProperty("names") JImmutableRandomAccessList<String> names)
        {
            this.names = names;
        }

        public JImmutableRandomAccessList<String> getNames()
        {
            return names;
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
            NamesBean namesBean = (NamesBean)o;
            return Objects.equals(names, namesBean.names);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(names);
        }
    }
}
