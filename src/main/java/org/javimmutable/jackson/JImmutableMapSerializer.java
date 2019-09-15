package org.javimmutable.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import org.javimmutable.collections.JImmutableMap;

import java.io.IOException;
import java.util.Map;

public class JImmutableMapSerializer
    extends ContainerSerializer<JImmutableMap<?, ?>>
    implements ContextualSerializer
{
    private final MapSerializer map;

    public JImmutableMapSerializer(MapSerializer map)
    {
        super(JImmutableMap.class, false);
        this.map = map;
    }

    @Override
    public JavaType getContentType()
    {
        return map.getContentType();
    }

    @Override
    public JsonSerializer<?> getContentSerializer()
    {
        return map.getContentSerializer();
    }

    @Override
    public boolean hasSingleElement(JImmutableMap<?, ?> value)
    {
        return map.hasSingleElement(value.getMap());
    }

    @Override
    protected JImmutableMapSerializer _withValueTypeSerializer(TypeSerializer vts)
    {
        return new JImmutableMapSerializer(map._withValueTypeSerializer(vts));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
                                              BeanProperty property)
        throws JsonMappingException
    {
        final JsonSerializer<?> mapSer = map.createContextual(prov, property);
        return new JsonSerializer<Object>()
        {
            @SuppressWarnings("unchecked")
            private final JsonSerializer<Map<?, ?>> callableSerializer = (JsonSerializer<Map<?, ?>>)mapSer;

            @Override
            public void serialize(Object value,
                                  JsonGenerator gen,
                                  SerializerProvider serializers)
                throws IOException
            {
                JImmutableMap jm = (JImmutableMap)value;
                callableSerializer.serialize(jm.getMap(), gen, serializers);
            }
        };
    }

    @Override
    public void serialize(JImmutableMap<?, ?> value,
                          JsonGenerator gen,
                          SerializerProvider provider)
        throws IOException
    {
        map.serialize(value.getMap(), gen, provider);
    }
}
