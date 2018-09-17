/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sun.xml.fastinfoset.streambuffer;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public final class AccessibleStringBuilder {
    
    /**
     * The value is used for character storage.
     */
    char value[];

    /** 
     * The count is the number of characters used.
     */
    int count;

    /** 
     * This no-arg constructor is necessary for serialization of subclasses.
     */
    public AccessibleStringBuilder() {
        this(32);
    }

    /** 
     * Creates an AbstractStringBuilder of the specified capacity.
     */
    public AccessibleStringBuilder(int capacity) {
        value = new char[capacity];
    }

    public int length() {
	return count;
    }

    public int capacity() {
	return value.length;
    }
    
    public void ensureCapacity(int minimumCapacity) {
	if (minimumCapacity > value.length) {
	    expandCapacity(minimumCapacity);
	}
    }
    
    void expandCapacity(int minimumCapacity) {
	int newCapacity = (value.length + 1) * 2;
        if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;
        } else if (minimumCapacity > newCapacity) {
	    newCapacity = minimumCapacity;
	}	
	char newValue[] = new char[newCapacity];
	System.arraycopy(value, 0, newValue, 0, count);
	value = newValue;
    }

    public void setLength(int newLength) {
	if (newLength < 0)
	    throw new StringIndexOutOfBoundsException(newLength);
	if (newLength > value.length)
	    expandCapacity(newLength);

	if (count < newLength) {
	    for (; count < newLength; count++)
		value[count] = '\0';
	} else {
            count = newLength;
        }
    }

    public AccessibleStringBuilder append(char str[]) { 
	int newCount = count + str.length;
	if (newCount > value.length)
	    expandCapacity(newCount);
        System.arraycopy(str, 0, value, count, str.length);
        count = newCount;
        return this;
    }

    public AccessibleStringBuilder append(char str[], int offset, int len) {
        int newCount = count + len;
	if (newCount > value.length)
	    expandCapacity(newCount);
	System.arraycopy(str, offset, value, count, len);
	count = newCount;
	return this;
    }

    public String toString() {
	return new String(value, 0, count);
    }

    public char[] getValue() {
        return value;
    }
}
