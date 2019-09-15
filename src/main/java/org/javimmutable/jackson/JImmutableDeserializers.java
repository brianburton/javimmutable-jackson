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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import org.javimmutable.collections.Insertable;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.util.JImmutables;

import static org.javimmutable.collections.util.JImmutables.*;

/**
 * Deserializers implementation that creates InsertableDeserializers for JImmutableList
 * and JImmutableSet.  Recognizes annotations to create hash, tree, or insert order sets.
 */
public class JImmutableDeserializers
    extends Deserializers.Base
{
    @Override
    public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type,
                                                       DeserializationConfig config,
                                                       BeanDescription beanDesc,
                                                       KeyDeserializer keyDeserializer,
                                                       TypeDeserializer elementTypeDeserializer,
                                                       JsonDeserializer<?> elementDeserializer)
        throws JsonMappingException
    {
        if (type.isTypeOrSubTypeOf(JImmutableMap.class)) {
            final JavaType keyType = type.getKeyType();
            if (!keyType.isTypeOrSubTypeOf(String.class)) {
                throw new IllegalArgumentException("JImmutableMaps must have String keys (" + keyType.getRawClass().getName() + ")");
            }
            return new JImmutableMapDeserializer<>(type, keyDeserializer, elementDeserializer, elementTypeDeserializer, JImmutables::mapBuilder);
        }
        return super.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
    }

    @Override
    public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type,
                                                              DeserializationConfig config,
                                                              BeanDescription beanDesc,
                                                              TypeDeserializer elementTypeDeserializer,
                                                              JsonDeserializer<?> elementDeserializer)
        throws JsonMappingException
    {
        if (type.isTypeOrSubTypeOf(Insertable.class)) {
            if (type.isTypeOrSubTypeOf(JImmutableList.class)) {
                return new InsertableDeserializer<>(type, elementDeserializer, elementTypeDeserializer, false, list(), list(), list());
            } else if (type.isTypeOrSubTypeOf(JImmutableSet.class)) {
                return new InsertableDeserializer<>(type, elementDeserializer, elementTypeDeserializer, false, set(), sortedSet(), insertOrderSet());
            }
            throw new IllegalArgumentException("Class is not supported: " + type.getRawClass().getName());
        }
        return super.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
    }
}
