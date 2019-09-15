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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import org.javimmutable.collections.JImmutableMap;

import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * Deserializer to populate an empty Insertable object in a generic way.
 * Can be used with any collection that implements Insertable interface.
 */
@Immutable
public class JImmutableMapDeserializer<T extends JImmutableMap<Object, Object>>
    extends StdDeserializer<T>
    implements ContextualDeserializer
{
    private final MapLikeType mapType;
    private final KeyDeserializer keyDeserializer;
    private final JsonDeserializer valueDeserializer;
    private final TypeDeserializer typeDeserializer;
    private final Supplier<JImmutableMap.Builder<Object,Object>> builderFactory;

    public JImmutableMapDeserializer(MapLikeType mapType,
                                     KeyDeserializer keyDeserializer,
                                     JsonDeserializer valueDeserializer,
                                     TypeDeserializer typeDeserializer,
                                     Supplier<JImmutableMap.Builder<Object, Object>> builderFactory)
    {
        super(mapType);
        this.mapType = mapType;
        this.keyDeserializer = keyDeserializer;
        this.valueDeserializer = valueDeserializer;
        this.typeDeserializer = typeDeserializer;
        this.builderFactory = builderFactory;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context,
                                                BeanProperty property)
        throws JsonMappingException
    {
        KeyDeserializer keyDeserializer = this.keyDeserializer;
        if (keyDeserializer == null) {
            keyDeserializer = context.findKeyDeserializer(mapType.getKeyType(), property);
        }

        JsonDeserializer<?> valueDeserializer = this.valueDeserializer;
        if (valueDeserializer == null) {
            valueDeserializer = context.findContextualValueDeserializer(mapType.getContentType(), property);
        }

        TypeDeserializer typeDeserializer = this.typeDeserializer;
        if (typeDeserializer != null) {
            typeDeserializer = typeDeserializer.forProperty(property);
        }

        return new JImmutableMapDeserializer<>(mapType, keyDeserializer, valueDeserializer, typeDeserializer, builderFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser parser,
                         DeserializationContext context)
        throws IOException, JsonProcessingException
    {
        if (!parser.isExpectedStartObjectToken()) {
            context.handleUnexpectedToken(mapType.getRawClass(), parser);
            throw new IOException("expected array start token");
        }
        
        final JImmutableMap.Builder<Object,Object> builder = builderFactory.get();
        while (true) {
            JsonToken token = parser.nextToken();
            if (token == JsonToken.END_OBJECT) {
                break;
            }
            if (token != JsonToken.FIELD_NAME) {
                context.handleUnexpectedToken(mapType.getRawClass(), parser);
                throw new IOException("expected field name");
            }
            Object key = deserializeKeyToken(parser, context);
            Object value = deserializeValueToken(parser, context, parser.nextToken());
            builder.add(key, value);
        }
        return (T)builder.build();
    }

    private Object deserializeKeyToken(JsonParser parser,
                                       DeserializationContext context)
        throws IOException
    {
        final String field = parser.getCurrentName();
        if (keyDeserializer == null) {
            return field;
        } else {
            return keyDeserializer.deserializeKey(field, context);
        }
    }

    private Object deserializeValueToken(JsonParser parser,
                                         DeserializationContext context,
                                         JsonToken token)
        throws IOException
    {
        Object value;
        if (token == JsonToken.VALUE_NULL) {
            value = null;
        } else if (typeDeserializer == null) {
            value = valueDeserializer.deserialize(parser, context);
        } else {
            value = valueDeserializer.deserializeWithType(parser, context, typeDeserializer);
        }
        return value;
    }
}
