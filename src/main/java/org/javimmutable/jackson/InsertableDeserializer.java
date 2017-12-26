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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import org.javimmutable.collections.Insertable;

import javax.annotation.concurrent.Immutable;
import java.io.IOException;

@Immutable
public class InsertableDeserializer<T extends Insertable>
    extends StdDeserializer<T>
    implements ContextualDeserializer
{
    private final CollectionLikeType collectionType;
    private final JsonDeserializer valueDeserializer;
    private final TypeDeserializer typeDeserializer;
    private final boolean acceptSingleValue;
    private final T empty;

    public InsertableDeserializer(CollectionLikeType collectionType,
                                  JsonDeserializer valueDeserializer,
                                  TypeDeserializer typeDeserializer,
                                  boolean acceptSingleValue,
                                  T empty)
    {
        super(collectionType);
        this.collectionType = collectionType;
        this.valueDeserializer = valueDeserializer;
        this.typeDeserializer = typeDeserializer;
        this.acceptSingleValue = acceptSingleValue;
        this.empty = empty;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context,
                                                BeanProperty property)
        throws JsonMappingException
    {
        JsonDeserializer<?> valueDeserializer = this.valueDeserializer;
        if (valueDeserializer == null) {
            valueDeserializer = context.findContextualValueDeserializer(collectionType.getContentType(), property);
        }

        TypeDeserializer typeDeserializer = this.typeDeserializer;
        if (typeDeserializer != null) {
            typeDeserializer = typeDeserializer.forProperty(property);
        }

        boolean acceptSingleValue = context.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return new InsertableDeserializer<>(collectionType, valueDeserializer, typeDeserializer, acceptSingleValue, empty);
    }

    @Override
    public T deserialize(JsonParser parser,
                         DeserializationContext context)
        throws IOException, JsonProcessingException
    {
        if (parser.isExpectedStartArrayToken()) {
            return deserializeArrayValues(parser, context);
        } else if (acceptSingleValue) {
            return deserializeSingleValue(parser, context);
        } else {
            throw context.mappingException(collectionType.getRawClass());
        }
    }

    @SuppressWarnings("unchecked")
    private T deserializeSingleValue(JsonParser parser,
                                     DeserializationContext context)
        throws IOException
    {
        Object value = deserializeToken(parser, context, parser.getCurrentToken());
        return (T)empty.insert(value);
    }

    @SuppressWarnings("unchecked")
    private T deserializeArrayValues(JsonParser parser,
                                     DeserializationContext context)
        throws IOException
    {
        T result = empty;

        JsonToken token;
        while ((token = parser.nextToken()) != JsonToken.END_ARRAY) {
            Object value = deserializeToken(parser, context, token);
            result = (T)result.insert(value);
        }

        return result;
    }

    private Object deserializeToken(JsonParser parser,
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
