/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.geospatial.index.mapper.shape;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.lucene.index.IndexableField;
import org.opensearch.geometry.Geometry;
import org.opensearch.geometry.GeometryVisitor;
import org.opensearch.index.mapper.AbstractGeometryFieldMapper;
import org.opensearch.index.mapper.ParseContext;

/**
 * Converts geometries into Lucene-compatible form for indexing in a shape field.
 */
public class ShapeIndexer implements AbstractGeometryFieldMapper.Indexer<Geometry, Geometry> {

    private final GeometryVisitor<IndexableField[], RuntimeException> indexableFieldsVisitor;
    private final GeometryVisitor<Geometry, RuntimeException> supportVisitor;

    public ShapeIndexer(
        GeometryVisitor<Geometry, RuntimeException> supportVisitor,
        GeometryVisitor<IndexableField[], RuntimeException> indexableFieldsVisitor
    ) {
        this.supportVisitor = Objects.requireNonNull(supportVisitor, "support visitor param cannot be null");
        this.indexableFieldsVisitor = Objects.requireNonNull(indexableFieldsVisitor, "indexable field visitor param cannot be null");
        ;
    }

    @Override
    public Geometry prepareForIndexing(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.visit(supportVisitor);
    }

    @Override
    public Class<Geometry> processedClass() {
        return Geometry.class;
    }

    @Override
    public List<IndexableField> indexShape(ParseContext parseContext, Geometry geometry) {
        return Arrays.asList(geometry.visit(indexableFieldsVisitor));
    }
}