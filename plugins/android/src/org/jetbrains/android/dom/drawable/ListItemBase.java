package org.jetbrains.android.dom.drawable;

import com.intellij.util.xml.Convert;
import org.jetbrains.android.dom.AndroidAttributeValue;
import org.jetbrains.android.dom.ResourceType;
import org.jetbrains.android.dom.converters.ResourceReferenceConverter;
import org.jetbrains.android.dom.resources.ResourceValue;

import java.util.List;

/**
 * @author Eugene.Kudelevsky
 */
public interface ListItemBase extends DrawableDomElement {
  @Convert(ResourceReferenceConverter.class)
    @ResourceType("drawable")
    AndroidAttributeValue<ResourceValue> getDrawable();

    List<BitmapOrNinePatchElement> getBitmaps();

    List<Shape> getShapes();

    List<InsetOrClipOrScale> getClips();
}
