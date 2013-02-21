/*
Copyright (c) 2013, ENERGY DEVELOPMENT UNIT (INFORMATION TECHNOLOGY)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, 
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, 
      this list of conditions and the following disclaimer in the documentation 
      and/or other materials provided with the distribution.
    * Neither the name of the DEPARTMENT OF ENERGY AND CLIMATE CHANGE nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/
package net.foxopen.foxydocs.model;

import java.io.IOException;
import java.util.List;

import net.foxopen.foxydocs.model.abstractObject.AbstractModelObject;

import org.jdom2.Element;
import org.jdom2.located.Located;

public class DocumentedElement extends DocEntry {
  private String previousDocumentationContent;
  private String name;
  private int lineNumber = -1;

  public DocumentedElement(Element node, AbstractModelObject parent) {
    super(node, parent);
   
    // Extract the line number. Must use SAX and a located element
    if (node instanceof Located) {
      Located locatedNode = (Located) node;
      lineNumber = locatedNode.getLine();
    }

   
    // Create documentation
    // DocEntry newDoc = new DocEntry(docNode, get);
    previousDocumentationContent = this.toString();

    String nodeName = node.getAttributeValue("name");
    if (nodeName == null) {
      setName(node.getName());
    } else {
      setName(nodeName);
    }
  }

  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public String getCode() throws IOException {
    return getParent().getCode();
  }

  @Override
  public String getName() {
    return name + " " + (isDirty() ? "*" : "");
  }

  @Override
  public synchronized boolean isDirty() {
    return !toString().equals(previousDocumentationContent);
  }

  @Override
  public void save() throws Exception {
    if (!isDirty())
      return;

    // New become old
    previousDocumentationContent = toString();
    firePropertyChange("dirty", true, isDirty());
    firePropertyChange("status", -1, getStatus());
  }

  public void setName(String name) {
    if (name == null || name.trim() == "")
      throw new IllegalArgumentException("Entry name is null");
    firePropertyChange("name", this.name, this.name = name);
  }

  @Override
  public String getDocumentation() {
    return getDescription()+getComments()+getPrecondition();
  }
  
  @Override
  public List<AbstractModelObject> getChildren() {
    // No children at this point
    return null;
  }

  @Override
  public boolean getHasChildren() {
    return false;
  }
}
