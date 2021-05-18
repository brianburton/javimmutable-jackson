///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.IterableSerializer;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;

import java.util.Collections;

/**
 * Serializers implementation that creates serializer instances for JImmutableMap,
 * JImmutableLst, and JImmutableSet.
 */
public class JImmutableSerializers
    extends Serializers.Base
{
    @Override
    public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config,
                                                   MapLikeType type,
                                                   BeanDescription beanDesc,
                                                   JsonSerializer<Object> keySerializer,
                                                   TypeSerializer elementTypeSerializer,
                                                   JsonSerializer<Object> elementValueSerializer)
    {
        if (type.isTypeOrSubTypeOf(JImmutableMap.class)) {
            MapSerializer map = MapSerializer.construct(Collections.emptySet(),
                                                        type,
                                                        false,
                                                        elementTypeSerializer,
                                                        keySerializer,
                                                        elementValueSerializer,
                                                        null);
            return new JImmutableMapSerializer(map);
        }
        return super.findMapLikeSerializer(config, type, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
    }

    @Override
    public JsonSerializer<?> findCollectionLikeSerializer(SerializationConfig config,
                                                          CollectionLikeType type,
                                                          BeanDescription beanDesc,
                                                          TypeSerializer elementTypeSerializer,
                                                          JsonSerializer<Object> elementValueSerializer)
    {

        if (type.isTypeOrSubTypeOf(JImmutableList.class) || type.isTypeOrSubTypeOf(JImmutableSet.class)) {
            return new IterableSerializer(type.getContentType(), false, elementTypeSerializer);
        }
        return super.findCollectionLikeSerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
    }
}
