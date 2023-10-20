/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jvnet.fastinfoset;

import java.io.OutputStream;
import java.util.Map;

/**
 * A general interface for serializers of fast infoset documents.
 *
 * <p>
 * This interface contains common methods that are not specific to any
 * API associated with the serialization of XML Infoset to fast infoset
 * documents.
 * 
 * @author Paul.Sandoz@Sun.Com
 */
public interface FastInfosetSerializer {
    /**
     * The feature to ignore the document type declaration and the 
     * internal subset.
     * <p>
     * The default value is false. If true a serializer shall ignore document
     * type declaration and the internal subset.
     */
    String IGNORE_DTD_FEATURE =
        "http://jvnet.org/fastinfoset/serializer/feature/ignore/DTD";
    
    /**
     * The feature to ignore comments.
     * <p>
     * The default value is false. If true a serializer shall ignore comments
     * and shall not serialize them.
     */
    String IGNORE_COMMENTS_FEATURE =
        "http://jvnet.org/fastinfoset/serializer/feature/ignore/comments";

    /**
     * The feature to ignore processing instructions.
     * <p>
     * The default value is false. If true a serializer shall ignore processing
     * instructions and shall not serialize them.
     */
    String IGNORE_PROCESSING_INSTRUCTIONS_FEATURE =
        "http://jvnet.org/fastinfoset/serializer/feature/ignore/processingInstructions";
    
    /**
     * The feature to ignore text content that consists completely of white
     * space characters.
     * <p>
     * The default value is false. If true a serializer shall ignore text
     * content that consists completely of white space characters.
     */
    String IGNORE_WHITE_SPACE_TEXT_CONTENT_FEATURE =
        "http://jvnet.org/fastinfoset/serializer/feature/ignore/whiteSpaceTextContent";
    
    /**
     * The property name to be used for getting and setting the buffer size
     * of a parser.
     */
    String BUFFER_SIZE_PROPERTY =
        "http://jvnet.org/fastinfoset/parser/properties/buffer-size";

    /**
     * The property name to be used for getting and setting the 
     * Map containing encoding algorithms.
     *
     */
    String REGISTERED_ENCODING_ALGORITHMS_PROPERTY =
        "http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms";
    
   /**
     * The property name to be used for getting and setting the 
     * Map containing external vocabularies.
     *
     */
   String EXTERNAL_VOCABULARIES_PROPERTY =
        "http://jvnet.org/fastinfoset/parser/properties/external-vocabularies";
    
    /**
     * The default minimum size of the character content chunks,
     * that will be indexed.
     */
    int MIN_CHARACTER_CONTENT_CHUNK_SIZE = 0;
    
    /**
     * The default maximum size of the character content chunks,
     * that will be indexed.
     */
    int MAX_CHARACTER_CONTENT_CHUNK_SIZE = 32;

    /**
     * The default value for limit on the size of indexed Map for attribute values
     * Limit is measured in bytes not in number of entries
     */
    int CHARACTER_CONTENT_CHUNK_MAP_MEMORY_CONSTRAINT = Integer.MAX_VALUE;

    /**
     * The default minimum size of the attribute values, that will be indexed.
     */
    int MIN_ATTRIBUTE_VALUE_SIZE = 0;
    
    /**
     * The default maximum size of the attribute values, that will be indexed.
     */
    int MAX_ATTRIBUTE_VALUE_SIZE = 32;

    /**
     * The default value for limit on the size of indexed Map for attribute values
     * Limit is measured in bytes not in number of entries
     */
    int ATTRIBUTE_VALUE_MAP_MEMORY_CONSTRAINT = Integer.MAX_VALUE;

    /**
     * The character encoding scheme string for UTF-8.
     */
    String UTF_8 = "UTF-8";
    
    /**
     * The character encoding scheme string for UTF-16BE.
     */
    String UTF_16BE = "UTF-16BE";
    
    /**
     * Set the {@link #IGNORE_DTD_FEATURE}.
     * @param ignoreDTD true if the feature shall be ignored.
     */
    void setIgnoreDTD(boolean ignoreDTD);
    
    /**
     * Get the {@link #IGNORE_DTD_FEATURE}.
     * @return true if the feature is ignored, false otherwise.
     */
    boolean getIgnoreDTD();
    
    /**
     * Set the {@link #IGNORE_COMMENTS_FEATURE}.
     * @param ignoreComments true if the feature shall be ignored.
     */
    void setIgnoreComments(boolean ignoreComments);
    
    /**
     * Get the {@link #IGNORE_COMMENTS_FEATURE}.
     * @return true if the feature is ignored, false otherwise.
     */
    boolean getIgnoreComments();
    
    /**
     * Set the {@link #IGNORE_PROCESSING_INSTRUCTIONS_FEATURE}.
     * @param ignoreProcesingInstructions true if the feature shall be ignored.
     */
    void setIgnoreProcesingInstructions(boolean ignoreProcesingInstructions);
    
    /**
     * Get the {@link #IGNORE_PROCESSING_INSTRUCTIONS_FEATURE}.
     * @return true if the feature is ignored, false otherwise.
     */
    boolean getIgnoreProcesingInstructions();
    
    /**
     * Set the {@link #IGNORE_WHITE_SPACE_TEXT_CONTENT_FEATURE}.
     * @param ignoreWhiteSpaceTextContent true if the feature shall be ignored.
     */
    void setIgnoreWhiteSpaceTextContent(boolean ignoreWhiteSpaceTextContent);
    
    /**
     * Get the {@link #IGNORE_WHITE_SPACE_TEXT_CONTENT_FEATURE}.
     * @return true if the feature is ignored, false otherwise.
     */
    boolean getIgnoreWhiteSpaceTextContent();
    
    /**
     * Sets the character encoding scheme.
     * <p>
     * The character encoding can be either UTF-8 or UTF-16BE for the
     * the encoding of chunks of CIIs, the [normalized value]
     * property of attribute information items, comment information
     * items and processing instruction information items.
     *
     * @param characterEncodingScheme The set of registered algorithms.
     */
    void setCharacterEncodingScheme(String characterEncodingScheme);
    
    /**
     * Gets the character encoding scheme.
     *
     * @return The character encoding scheme.
     */
    String getCharacterEncodingScheme();
    
    /**
     * Sets the set of registered encoding algorithms.
     *
     * @param algorithms The set of registered algorithms.
     */
    void setRegisteredEncodingAlgorithms(Map<String, EncodingAlgorithm> algorithms);
    
    /**
     * Gets the set of registered encoding algorithms.
     *
     * @return The set of registered algorithms.
     */
    Map<String, EncodingAlgorithm> getRegisteredEncodingAlgorithms();
    
    /**
     * Gets the minimum size of character content chunks
     * that will be indexed.
     *
     * @return The minimum character content chunk size.
     */
    int getMinCharacterContentChunkSize();

    /**
     * Sets the minimum size of character content chunks
     * that will be indexed.
     *
     * @param size the minimum character content chunk size.
     */
    void setMinCharacterContentChunkSize(int size);

    /**
     * Gets the maximum size of character content chunks
     * that might be indexed.
     *
     * @return The maximum character content chunk size.
     */
    int getMaxCharacterContentChunkSize();

    /**
     * Sets the maximum size of character content chunks
     * that might be indexed.
     *
     * @param size the maximum character content chunk size.
     */
    void setMaxCharacterContentChunkSize(int size);
    
    /**
     * Gets the limit on the memory size, allocated for indexed character
     * content chunks.
     *
     * @return the limit on the memory size, allocated for indexed character
     * content chunks.
     */
    int getCharacterContentChunkMapMemoryLimit();

    /**
     * Sets the limit on the memory size, allocated for indexed character
     * content chunks.
     *
     * @param size the limit on the memory size, allocated for indexed character
     * content chunks.
     */
    void setCharacterContentChunkMapMemoryLimit(int size);
    
    /**
     * Gets the minimum size of attribute values
     * that will be indexed.
     *
     * @return The minimum attribute values size.
     */
    int getMinAttributeValueSize();

    /**
     * Sets the minimum size of attribute values
     * that will be indexed.
     *
     * @param size the minimum attribute values size.
     */
    void setMinAttributeValueSize(int size);
    
    /**
     * Gets the maximum size of attribute values
     * that will be indexed.
     *
     * @return The maximum attribute values size.
     */
    int getMaxAttributeValueSize();

    /**
     * Sets the maximum size of attribute values
     * that will be indexed.
     *
     * @param size the maximum attribute values size.
     */
    void setMaxAttributeValueSize(int size);

    /**
     * Gets the limit on the memory size of Map of attribute values
     * that will be indexed.
     *
     * @return The attribute value size limit.
     */
    int getAttributeValueMapMemoryLimit();

    /**
     * Sets the limit on the memory size of Map of attribute values
     * that will be indexed.
     *
     * @param size The attribute value size limit. Any value less
     * that a length of size limit will be indexed.
     */
    void setAttributeValueMapMemoryLimit(int size);
    
    /**
     * Set the external vocabulary that shall be used when serializing.
     * 
     * @param v the vocabulary. 
     */
    void setExternalVocabulary(ExternalVocabulary v);
    
    /**
     * Set the application data to be associated with the serializer vocabulary.
     * 
     * @param data the application data. 
     */
    void setVocabularyApplicationData(VocabularyApplicationData data);
    
    /**
     * Get the application data associated with the serializer vocabulary.
     * 
     * @return the application data. 
     */
    VocabularyApplicationData getVocabularyApplicationData();
    
    /**
     * Reset the serializer for reuse serializing another XML infoset.
     */
    void reset();
        
    /**
     * Set the OutputStream to serialize the XML infoset to a 
     * fast infoset document.
     *
     * @param s the OutputStream where the fast infoset document is written to.
     */
    void setOutputStream(OutputStream s);
}
