/*
 * Adapted from https://github.com/Shadows-of-Fire/Plants/blob/master/
 * src/main/java/shadows/plants2/client/Transformer.java
 * Original license are duplicated below.
 *
 * Copyright (c) 2017 Brennan Ward
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software. Any derivative works using
 * code from this project must be open source.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package snownee.cuisine.client.model;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class QuadTransformer extends VertexTransformer
{
    private final Matrix4f transform;
    private final Matrix3f normalTransform;

    public QuadTransformer(TRSRTransformation transform, VertexFormat vertexFormat)
    {
        super(new UnpackedBakedQuad.Builder(vertexFormat));
        this.transform = transform.getMatrix();
        this.normalTransform = new Matrix3f();
        this.transform.getRotationScale(this.normalTransform);
        this.normalTransform.invert();
        this.normalTransform.transpose();
    }

    @Override
    public void put(int element, float... data) {
        VertexFormatElement.EnumUsage usage = parent.getVertexFormat().getElement(element).getUsage();

        // transform normals and position
        if (usage == VertexFormatElement.EnumUsage.POSITION && data.length >= 3) {
            Vector4f vec = new Vector4f(data);
            vec.setW(1.0f);
            transform.transform(vec);
            data = new float[4];
            vec.get(data);
        } else if (usage == VertexFormatElement.EnumUsage.NORMAL && data.length >= 3) {
            Vector3f vec = new Vector3f(data);
            normalTransform.transform(vec);
            vec.normalize();
            data = new float[4];
            vec.get(data);
        }
        super.put(element, data);
    }

    public UnpackedBakedQuad build() {
        return ((UnpackedBakedQuad.Builder) parent).build();
    }
}
