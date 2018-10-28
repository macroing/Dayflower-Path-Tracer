/**
 * Copyright 2015 - 2018 J&#246;rgen Lundgren
 * 
 * This file is part of Dayflower.
 * 
 * Dayflower is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Dayflower is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dayflower. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dayflower.pathtracer.math;

import static org.dayflower.pathtracer.math.MathF.abs;
import static org.dayflower.pathtracer.math.MathF.cos;
import static org.dayflower.pathtracer.math.MathF.sin;
import static org.dayflower.pathtracer.math.MathF.tan;

import java.util.Objects;

import org.dayflower.pathtracer.util.Strings;

/**
 * A {@code Matrix44F} denotes a matrix with four columns and four rows that is stored in row-major order.
 * <p>
 * This class is immutable and therefore thread-safe.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Matrix44F {
	/**
	 * The element at index {@code 0} or row {@code 0} and column {@code 0}.
	 */
	public final float element00;
	
	/**
	 * The element at index {@code 1} or row {@code 0} and column {@code 1}.
	 */
	public final float element01;
	
	/**
	 * The element at index {@code 2} or row {@code 0} and column {@code 2}.
	 */
	public final float element02;
	
	/**
	 * The element at index {@code 3} or row {@code 0} and column {@code 3}.
	 */
	public final float element03;
	
	/**
	 * The element at index {@code 4} or row {@code 1} and column {@code 0}.
	 */
	public final float element10;
	
	/**
	 * The element at index {@code 5} or row {@code 1} and column {@code 1}.
	 */
	public final float element11;
	
	/**
	 * The element at index {@code 6} or row {@code 1} and column {@code 2}.
	 */
	public final float element12;
	
	/**
	 * The element at index {@code 7} or row {@code 1} and column {@code 3}.
	 */
	public final float element13;
	
	/**
	 * The element at index {@code 8} or row {@code 2} and column {@code 0}.
	 */
	public final float element20;
	
	/**
	 * The element at index {@code 9} or row {@code 2} and column {@code 1}.
	 */
	public final float element21;
	
	/**
	 * The element at index {@code 10} or row {@code 2} and column {@code 2}.
	 */
	public final float element22;
	
	/**
	 * The element at index {@code 11} or row {@code 2} and column {@code 3}.
	 */
	public final float element23;
	
	/**
	 * The element at index {@code 12} or row {@code 3} and column {@code 0}.
	 */
	public final float element30;
	
	/**
	 * The element at index {@code 13} or row {@code 3} and column {@code 1}.
	 */
	public final float element31;
	
	/**
	 * The element at index {@code 14} or row {@code 3} and column {@code 2}.
	 */
	public final float element32;
	
	/**
	 * The element at index {@code 15} or row {@code 3} and column {@code 3}.
	 */
	public final float element33;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Matrix44F} instance representing the identity matrix.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new Matrix44F(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
	 * }
	 * </pre>
	 */
	public Matrix44F() {
		this(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
	/**
	 * Constructs a new {@code Matrix44F} instance given its element values.
	 * 
	 * @param element00 the element at index {@code 0} or row {@code 0} and column {@code 0}
	 * @param element01 the element at index {@code 1} or row {@code 0} and column {@code 1}
	 * @param element02 the element at index {@code 2} or row {@code 0} and column {@code 2}
	 * @param element03 the element at index {@code 3} or row {@code 0} and column {@code 3}
	 * @param element10 the element at index {@code 4} or row {@code 1} and column {@code 0}
	 * @param element11 the element at index {@code 5} or row {@code 1} and column {@code 1}
	 * @param element12 the element at index {@code 6} or row {@code 1} and column {@code 2}
	 * @param element13 the element at index {@code 7} or row {@code 1} and column {@code 3}
	 * @param element20 the element at index {@code 8} or row {@code 2} and column {@code 0}
	 * @param element21 the element at index {@code 9} or row {@code 2} and column {@code 1}
	 * @param element22 the element at index {@code 10} or row {@code 2} and column {@code 2}
	 * @param element23 the element at index {@code 11} or row {@code 2} and column {@code 3}
	 * @param element30 the element at index {@code 12} or row {@code 3} and column {@code 0}
	 * @param element31 the element at index {@code 13} or row {@code 3} and column {@code 1}
	 * @param element32 the element at index {@code 14} or row {@code 3} and column {@code 2}
	 * @param element33 the element at index {@code 15} or row {@code 3} and column {@code 3}
	 */
	public Matrix44F(final float element00, final float element01, final float element02, final float element03, final float element10, final float element11, final float element12, final float element13, final float element20, final float element21, final float element22, final float element23, final float element30, final float element31, final float element32, final float element33) {
		this.element00 = element00;
		this.element01 = element01;
		this.element02 = element02;
		this.element03 = element03;
		this.element10 = element10;
		this.element11 = element11;
		this.element12 = element12;
		this.element13 = element13;
		this.element20 = element20;
		this.element21 = element21;
		this.element22 = element22;
		this.element23 = element23;
		this.element30 = element30;
		this.element31 = element31;
		this.element32 = element32;
		this.element33 = element33;
	}
	
	/**
	 * Constructs a new {@code Matrix44F} instance given its element values.
	 * <p>
	 * Calling this constructor is equivalent to the following (assuming {@code elements} is called {@code e} for brevity):
	 * <pre>
	 * {@code
	 * new Matrix44F(e[0], e[1], e[2], e[3], e[4], e[5], e[6], e[7], e[8], e[9], e[10], e[11], e[12], e[13], e[14], e[15])
	 * }
	 * </pre>
	 * If {@code elements} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code elements.length} is less than {@code 16}, an {@code ArrayIndexOutOfBoundsException} will be thrown.
	 * 
	 * @param elements a one-dimensional {@code float} array with the element values to use
	 * @throws ArrayIndexOutOfBoundsException thrown if, and only if, {@code elements.length} is less than {@code 16}
	 * @throws NullPointerException thrown if, and only if, {@code elements} is {@code null}
	 */
	public Matrix44F(final float[] elements) {
		this(elements[0], elements[1], elements[2], elements[3], elements[4], elements[5], elements[6], elements[7], elements[8], elements[9], elements[10], elements[11], elements[12], elements[13], elements[14], elements[15]);
	}
	
	/**
	 * Constructs a new {@code Matrix44F} instance given its element values.
	 * <p>
	 * Calling this constructor is equivalent to the following (assuming {@code elements} is called {@code e} for brevity):
	 * <pre>
	 * {@code
	 * new Matrix44F(e[0][0], e[0][1], e[0][2], e[0][3], e[1][0], e[1][1], e[1][2], e[1][3], e[2][0], e[2][1], e[2][2], e[2][3], e[3][0], e[3][1], e[3][2], e[3][3])
	 * }
	 * </pre>
	 * If either {@code elements} or its elements are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code elements.length} is less than {@code 4} or {@code elements[i].length} is less than {@code 4}, where {@code 0 <= i < 4}, an {@code ArrayIndexOutOfBoundsException} will be thrown.
	 * 
	 * @param elements a two-dimensional {@code float} array with the element values to use
	 * @throws ArrayIndexOutOfBoundsException thrown if, and only if, {@code elements.length} is less than {@code 4} or {@code elements[i].length} is less than {@code 4}, where {@code 0 <= i < 4}
	 * @throws NullPointerException thrown if, and only if, either {@code elements} or its elements are {@code null}
	 */
	public Matrix44F(final float[][] elements) {
		this(elements[0][0], elements[0][1], elements[0][2], elements[0][3], elements[1][0], elements[1][1], elements[1][2], elements[1][3], elements[2][0], elements[2][1], elements[2][2], elements[2][3], elements[3][0], elements[3][1], elements[3][2], elements[3][3]);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a new {@code Matrix44F} that represents the inverse of this {@code Matrix44F} instance.
	 * <p>
	 * If this {@code Matrix44F} cannot be inverted, an {@code IllegalStateException} will be thrown.
	 * 
	 * @return a new {@code Matrix44F} that represents the inverse of this {@code Matrix44F} instance
	 * @throws IllegalStateException thrown if, and only if, this {@code Matrix44F} cannot be inverted
	 */
	public Matrix44F inverse() {
		final float a = this.element00 * this.element11 - this.element01 * this.element10;
		final float b = this.element00 * this.element12 - this.element02 * this.element10;
		final float c = this.element00 * this.element13 - this.element03 * this.element10;
		final float d = this.element01 * this.element12 - this.element02 * this.element11;
		final float e = this.element01 * this.element13 - this.element03 * this.element11;
		final float f = this.element02 * this.element13 - this.element03 * this.element12;
		final float g = this.element20 * this.element31 - this.element21 * this.element30;
		final float h = this.element20 * this.element32 - this.element22 * this.element30;
		final float i = this.element20 * this.element33 - this.element23 * this.element30;
		final float j = this.element21 * this.element32 - this.element22 * this.element31;
		final float k = this.element21 * this.element33 - this.element23 * this.element31;
		final float l = this.element22 * this.element33 - this.element23 * this.element32;
		
		final float determinant = a * l - b * k + c * j + d * i - e * h + f * g;
		
		if(abs(determinant) < 1.0e-12F) {
			throw new IllegalStateException("This Matrix44F cannot be inverted!");
		}
		
		final float determinantReciprocal = 1.0F / determinant;
		
		final float element00 = (+this.element11 * l - this.element12 * k + this.element13 * j) * determinantReciprocal;
		final float element01 = (-this.element01 * l + this.element02 * k - this.element03 * j) * determinantReciprocal;
		final float element02 = (+this.element31 * f - this.element32 * e + this.element33 * d) * determinantReciprocal;
		final float element03 = (-this.element21 * f + this.element22 * e - this.element23 * d) * determinantReciprocal;
		final float element10 = (-this.element10 * l + this.element11 * i - this.element13 * h) * determinantReciprocal;
		final float element11 = (+this.element00 * l - this.element02 * i + this.element03 * h) * determinantReciprocal;
		final float element12 = (-this.element30 * f + this.element32 * c - this.element33 * b) * determinantReciprocal;
		final float element13 = (+this.element20 * f - this.element22 * c + this.element23 * b) * determinantReciprocal;
		final float element20 = (+this.element10 * k - this.element11 * i + this.element13 * g) * determinantReciprocal;
		final float element21 = (-this.element00 * k + this.element01 * i - this.element03 * g) * determinantReciprocal;
		final float element22 = (+this.element30 * e - this.element31 * c + this.element33 * a) * determinantReciprocal;
		final float element23 = (-this.element20 * e + this.element21 * c - this.element23 * a) * determinantReciprocal;
		final float element30 = (-this.element10 * j + this.element11 * h - this.element12 * g) * determinantReciprocal;
		final float element31 = (+this.element00 * j - this.element01 * h + this.element02 * g) * determinantReciprocal;
		final float element32 = (-this.element30 * d + this.element31 * b - this.element32 * a) * determinantReciprocal;
		final float element33 = (+this.element20 * d - this.element21 * b + this.element22 * a) * determinantReciprocal;
		
		return new Matrix44F(element00, element01, element02, element03, element10, element11, element12, element13, element20, element21, element22, element23, element30, element31, element32, element33);
	}
	
	/**
	 * Multiplies this {@code Matrix44F} instance by {@code matrix}.
	 * <p>
	 * Returns a new {@code Matrix44F} instance with the result of the multiplication.
	 * <p>
	 * If {@code matrix} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param matrix the {@code Matrix44F} to multiply this {@code Matrix44F} instance with
	 * @return a new {@code Matrix44F} instance with the result of the multiplication
	 * @throws NullPointerException thrown if, and only if, {@code matrix} is {@code null}
	 */
	public Matrix44F multiply(final Matrix44F matrix) {
		final float element00 = this.element00 * matrix.element00 + this.element01 * matrix.element10 + this.element02 * matrix.element20 + this.element03 * matrix.element30;
		final float element01 = this.element00 * matrix.element01 + this.element01 * matrix.element11 + this.element02 * matrix.element21 + this.element03 * matrix.element31;
		final float element02 = this.element00 * matrix.element02 + this.element01 * matrix.element12 + this.element02 * matrix.element22 + this.element03 * matrix.element32;
		final float element03 = this.element00 * matrix.element03 + this.element01 * matrix.element13 + this.element02 * matrix.element23 + this.element03 * matrix.element33;
		final float element10 = this.element10 * matrix.element00 + this.element11 * matrix.element10 + this.element12 * matrix.element20 + this.element13 * matrix.element30;
		final float element11 = this.element10 * matrix.element01 + this.element11 * matrix.element11 + this.element12 * matrix.element21 + this.element13 * matrix.element31;
		final float element12 = this.element10 * matrix.element02 + this.element11 * matrix.element12 + this.element12 * matrix.element22 + this.element13 * matrix.element32;
		final float element13 = this.element10 * matrix.element03 + this.element11 * matrix.element13 + this.element12 * matrix.element23 + this.element13 * matrix.element33;
		final float element20 = this.element20 * matrix.element00 + this.element21 * matrix.element10 + this.element22 * matrix.element20 + this.element23 * matrix.element30;
		final float element21 = this.element20 * matrix.element01 + this.element21 * matrix.element11 + this.element22 * matrix.element21 + this.element23 * matrix.element31;
		final float element22 = this.element20 * matrix.element02 + this.element21 * matrix.element12 + this.element22 * matrix.element22 + this.element23 * matrix.element32;
		final float element23 = this.element20 * matrix.element03 + this.element21 * matrix.element13 + this.element22 * matrix.element23 + this.element23 * matrix.element33;
		final float element30 = this.element30 * matrix.element00 + this.element31 * matrix.element10 + this.element32 * matrix.element20 + this.element33 * matrix.element30;
		final float element31 = this.element30 * matrix.element01 + this.element31 * matrix.element11 + this.element32 * matrix.element21 + this.element33 * matrix.element31;
		final float element32 = this.element30 * matrix.element02 + this.element31 * matrix.element12 + this.element32 * matrix.element22 + this.element33 * matrix.element32;
		final float element33 = this.element30 * matrix.element03 + this.element31 * matrix.element13 + this.element32 * matrix.element23 + this.element33 * matrix.element33;
		
		return new Matrix44F(element00, element01, element02, element03, element10, element11, element12, element13, element20, element21, element22, element23, element30, element31, element32, element33);
	}
	
	/**
	 * Returns a new {@code Matrix44F} that represents the transpose of this {@code Matrix44F} instance.
	 * 
	 * @return a new {@code Matrix44F} that represents the transpose of this {@code Matrix44F} instance
	 */
	public Matrix44F transpose() {
		return new Matrix44F(this.element00, this.element10, this.element20, this.element30, this.element01, this.element11, this.element21, this.element31, this.element02, this.element12, this.element22, this.element32, this.element03, this.element13, this.element23, this.element33);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Matrix44F} instance.
	 * 
	 * @return a {@code String} representation of this {@code Matrix44F} instance
	 */
	@Override
	public String toString() {
		final String element00 = Strings.toNonScientificNotation(this.element00);
		final String element01 = Strings.toNonScientificNotation(this.element01);
		final String element02 = Strings.toNonScientificNotation(this.element02);
		final String element03 = Strings.toNonScientificNotation(this.element03);
		final String element10 = Strings.toNonScientificNotation(this.element10);
		final String element11 = Strings.toNonScientificNotation(this.element11);
		final String element12 = Strings.toNonScientificNotation(this.element12);
		final String element13 = Strings.toNonScientificNotation(this.element13);
		final String element20 = Strings.toNonScientificNotation(this.element20);
		final String element21 = Strings.toNonScientificNotation(this.element21);
		final String element22 = Strings.toNonScientificNotation(this.element22);
		final String element23 = Strings.toNonScientificNotation(this.element23);
		final String element30 = Strings.toNonScientificNotation(this.element30);
		final String element31 = Strings.toNonScientificNotation(this.element31);
		final String element32 = Strings.toNonScientificNotation(this.element32);
		final String element33 = Strings.toNonScientificNotation(this.element33);
		
		return String.format("new Matrix44F(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", element00, element01, element02, element03, element10, element11, element12, element13, element20, element21, element22, element23, element30, element31, element32, element33);
	}
	
	/**
	 * Compares {@code object} to this {@code Matrix44F} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Matrix44F}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Matrix44F} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Matrix44F}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Matrix44F)) {
			return false;
		} else if(Float.compare(this.element00, Matrix44F.class.cast(object).element00) != 0) {
			return false;
		} else if(Float.compare(this.element01, Matrix44F.class.cast(object).element01) != 0) {
			return false;
		} else if(Float.compare(this.element02, Matrix44F.class.cast(object).element02) != 0) {
			return false;
		} else if(Float.compare(this.element03, Matrix44F.class.cast(object).element03) != 0) {
			return false;
		} else if(Float.compare(this.element10, Matrix44F.class.cast(object).element10) != 0) {
			return false;
		} else if(Float.compare(this.element11, Matrix44F.class.cast(object).element11) != 0) {
			return false;
		} else if(Float.compare(this.element12, Matrix44F.class.cast(object).element12) != 0) {
			return false;
		} else if(Float.compare(this.element13, Matrix44F.class.cast(object).element13) != 0) {
			return false;
		} else if(Float.compare(this.element20, Matrix44F.class.cast(object).element20) != 0) {
			return false;
		} else if(Float.compare(this.element21, Matrix44F.class.cast(object).element21) != 0) {
			return false;
		} else if(Float.compare(this.element22, Matrix44F.class.cast(object).element22) != 0) {
			return false;
		} else if(Float.compare(this.element23, Matrix44F.class.cast(object).element23) != 0) {
			return false;
		} else if(Float.compare(this.element30, Matrix44F.class.cast(object).element30) != 0) {
			return false;
		} else if(Float.compare(this.element31, Matrix44F.class.cast(object).element31) != 0) {
			return false;
		} else if(Float.compare(this.element32, Matrix44F.class.cast(object).element32) != 0) {
			return false;
		} else if(Float.compare(this.element33, Matrix44F.class.cast(object).element33) != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code Matrix44F} represents the identity matrix, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code Matrix44F} represents the identity matrix, {@code false} otherwise
	 */
	public boolean isIdentity() {
		final boolean isIdentity0 = MathF.equals(this.element00, 1.0F) && MathF.equals(this.element01, 0.0F) && MathF.equals(this.element02, 0.0F) && MathF.equals(this.element03, 0.0F);
		final boolean isIdentity1 = MathF.equals(this.element10, 0.0F) && MathF.equals(this.element11, 1.0F) && MathF.equals(this.element12, 0.0F) && MathF.equals(this.element13, 0.0F);
		final boolean isIdentity2 = MathF.equals(this.element20, 0.0F) && MathF.equals(this.element21, 0.0F) && MathF.equals(this.element22, 1.0F) && MathF.equals(this.element23, 0.0F);
		final boolean isIdentity3 = MathF.equals(this.element30, 0.0F) && MathF.equals(this.element31, 0.0F) && MathF.equals(this.element32, 0.0F) && MathF.equals(this.element33, 1.0F);
		
		return isIdentity0 && isIdentity1 && isIdentity2 && isIdentity3;
	}
	
	/**
	 * Returns the determinant of this {@code Matrix44F} instance.
	 * 
	 * @return the determinant of this {@code Matrix44F} instance
	 */
	public float determinant() {
		final float a = this.element00 * this.element11 - this.element01 * this.element10;
		final float b = this.element00 * this.element12 - this.element02 * this.element10;
		final float c = this.element00 * this.element13 - this.element03 * this.element10;
		final float d = this.element01 * this.element12 - this.element02 * this.element11;
		final float e = this.element01 * this.element13 - this.element03 * this.element11;
		final float f = this.element02 * this.element13 - this.element03 * this.element12;
		final float g = this.element20 * this.element31 - this.element21 * this.element30;
		final float h = this.element20 * this.element32 - this.element22 * this.element30;
		final float i = this.element20 * this.element33 - this.element23 * this.element30;
		final float j = this.element21 * this.element32 - this.element22 * this.element31;
		final float k = this.element21 * this.element33 - this.element23 * this.element31;
		final float l = this.element22 * this.element33 - this.element23 * this.element32;
		
		return a * l - b * k + c * j + d * i - e * h + f * g;
	}
	
	/**
	 * Returns the element at index {@code 0} or row {@code 0} and column {@code 0}.
	 * 
	 * @return the element at index {@code 0} or row {@code 0} and column {@code 0}
	 */
	public float getElement00() {
		return this.element00;
	}
	
	/**
	 * Returns the element at index {@code 1} or row {@code 0} and column {@code 1}.
	 * 
	 * @return the element at index {@code 1} or row {@code 0} and column {@code 1}
	 */
	public float getElement01() {
		return this.element01;
	}
	
	/**
	 * Returns the element at index {@code 2} or row {@code 0} and column {@code 2}.
	 * 
	 * @return the element at index {@code 2} or row {@code 0} and column {@code 2}
	 */
	public float getElement02() {
		return this.element02;
	}
	
	/**
	 * Returns the element at index {@code 3} or row {@code 0} and column {@code 3}.
	 * 
	 * @return the element at index {@code 3} or row {@code 0} and column {@code 3}
	 */
	public float getElement03() {
		return this.element03;
	}
	
	/**
	 * Returns the element at index {@code 4} or row {@code 1} and column {@code 0}.
	 * 
	 * @return the element at index {@code 4} or row {@code 1} and column {@code 0}
	 */
	public float getElement10() {
		return this.element10;
	}
	
	/**
	 * Returns the element at index {@code 5} or row {@code 1} and column {@code 1}.
	 * 
	 * @return the element at index {@code 5} or row {@code 1} and column {@code 1}
	 */
	public float getElement11() {
		return this.element11;
	}
	
	/**
	 * Returns the element at index {@code 6} or row {@code 1} and column {@code 2}.
	 * 
	 * @return the element at index {@code 6} or row {@code 1} and column {@code 2}
	 */
	public float getElement12() {
		return this.element12;
	}
	
	/**
	 * Returns the element at index {@code 7} or row {@code 1} and column {@code 3}.
	 * 
	 * @return the element at index {@code 7} or row {@code 1} and column {@code 3}
	 */
	public float getElement13() {
		return this.element13;
	}
	
	/**
	 * Returns the element at index {@code 8} or row {@code 2} and column {@code 0}.
	 * 
	 * @return the element at index {@code 8} or row {@code 2} and column {@code 0}
	 */
	public float getElement20() {
		return this.element20;
	}
	
	/**
	 * Returns the element at index {@code 9} or row {@code 2} and column {@code 1}.
	 * 
	 * @return the element at index {@code 9} or row {@code 2} and column {@code 1}
	 */
	public float getElement21() {
		return this.element21;
	}
	
	/**
	 * Returns the element at index {@code 10} or row {@code 2} and column {@code 2}.
	 * 
	 * @return the element at index {@code 10} or row {@code 2} and column {@code 2}
	 */
	public float getElement22() {
		return this.element22;
	}
	
	/**
	 * Returns the element at index {@code 11} or row {@code 2} and column {@code 3}.
	 * 
	 * @return the element at index {@code 11} or row {@code 2} and column {@code 3}
	 */
	public float getElement23() {
		return this.element23;
	}
	
	/**
	 * Returns the element at index {@code 12} or row {@code 3} and column {@code 0}.
	 * 
	 * @return the element at index {@code 12} or row {@code 3} and column {@code 0}
	 */
	public float getElement30() {
		return this.element30;
	}
	
	/**
	 * Returns the element at index {@code 13} or row {@code 3} and column {@code 1}.
	 * 
	 * @return the element at index {@code 13} or row {@code 3} and column {@code 1}
	 */
	public float getElement31() {
		return this.element31;
	}
	
	/**
	 * Returns the element at index {@code 14} or row {@code 3} and column {@code 2}.
	 * 
	 * @return the element at index {@code 14} or row {@code 3} and column {@code 2}
	 */
	public float getElement32() {
		return this.element32;
	}
	
	/**
	 * Returns the element at index {@code 15} or row {@code 3} and column {@code 3}.
	 * 
	 * @return the element at index {@code 15} or row {@code 3} and column {@code 3}
	 */
	public float getElement33() {
		return this.element33;
	}
	
	/**
	 * Returns this {@code Matrix44F} as a one-dimensional {@code float} array in row-major order.
	 * 
	 * @return this {@code Matrix44F} as a one-dimensional {@code float} array in row-major order
	 */
	public float[] toArray1D() {
		return new float[] {this.element00, this.element01, this.element02, this.element03, this.element10, this.element11, this.element12, this.element13, this.element20, this.element21, this.element22, this.element23, this.element30, this.element31, this.element32, this.element33};
	}
	
	/**
	 * Returns this {@code Matrix44F} as a two-dimensional {@code float} array in row-major order.
	 * 
	 * @return this {@code Matrix44F} as a two-dimensional {@code float} array in row-major order
	 */
	public float[][] toArray2D() {
		return new float[][] {new float[] {this.element00, this.element01, this.element02, this.element03}, new float[] {this.element10, this.element11, this.element12, this.element13}, new float[] {this.element20, this.element21, this.element22, this.element23}, new float[] {this.element30, this.element31, this.element32, this.element33}};
	}
	
	/**
	 * Returns a hash code for this {@code Matrix44F} instance.
	 * 
	 * @return a hash code for this {@code Matrix44F} instance
	 */
	@Override
	public int hashCode() {
		final Float element00 = Float.valueOf(this.element00);
		final Float element01 = Float.valueOf(this.element01);
		final Float element02 = Float.valueOf(this.element02);
		final Float element03 = Float.valueOf(this.element03);
		final Float element10 = Float.valueOf(this.element10);
		final Float element11 = Float.valueOf(this.element11);
		final Float element12 = Float.valueOf(this.element12);
		final Float element13 = Float.valueOf(this.element13);
		final Float element20 = Float.valueOf(this.element20);
		final Float element21 = Float.valueOf(this.element21);
		final Float element22 = Float.valueOf(this.element22);
		final Float element23 = Float.valueOf(this.element23);
		final Float element30 = Float.valueOf(this.element30);
		final Float element31 = Float.valueOf(this.element31);
		final Float element32 = Float.valueOf(this.element32);
		final Float element33 = Float.valueOf(this.element33);
		
		return Objects.hash(element00, element01, element02, element03, element10, element11, element12, element13, element20, element21, element22, element23, element30, element31, element32, element33);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a new {@code Matrix44F} from an {@link OrthoNormalBasis33F}.
	 * <p>
	 * If {@code orthoNormalBasis} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * u.x, v.x, w.x, 0
	 * u.y, v.y, w.y, 0
	 * u.z, v.z, w.z, 0
	 *   0,   0,   0, 1
	 * }
	 * </pre>
	 * 
	 * @param orthoNormalBasis an {@code OrthoNormalBasis33F}
	 * @return a new {@code Matrix44F} from an {@code OrthoNormalBasis33F}
	 * @throws NullPointerException thrown if, and only if, {@code orthoNormalBasis} is {@code null}
	 */
	public static Matrix44F fromOrthoNormalBasis(final OrthoNormalBasis33F orthoNormalBasis) {
		final Vector3F u = Vector3F.x().transform(orthoNormalBasis);
		final Vector3F v = Vector3F.y().transform(orthoNormalBasis);
		final Vector3F w = Vector3F.z().transform(orthoNormalBasis);
		
		return new Matrix44F(u.x, v.x, w.x, 0.0F, u.y, v.y, w.y, 0.0F, u.z, v.z, w.z, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
	/**
	 * Returns a {@code Matrix44F} for perspective viewing.
	 * <p>
	 * If {@code fieldOfView} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param fieldOfView an {@link AngleF} with field of view
	 * @param aspectRatio the aspect ratio to use
	 * @param zNear the Z-near to use
	 * @param zFar the Z-far to use
	 * @return a {@code Matrix44F} for perspective viewing
	 * @throws NullPointerException thrown if, and only if, {@code fieldOfView} is {@code null}
	 */
	public static Matrix44F perspective(final AngleF fieldOfView, final float aspectRatio, final float zNear, final float zFar) {
		final float focalLength = tan(fieldOfView.half().radians);
		
		return new Matrix44F(1.0F / (focalLength * aspectRatio), 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / focalLength, 0.0F, 0.0F, 0.0F, 0.0F, (-zNear - zFar) / (zNear - zFar), 2.0F * zFar * zNear / (zNear - zFar), 0.0F, 0.0F, 1.0F, 0.0F);
	}
	
//	TODO: Add Javadocs!
	public static Matrix44F rotate(final Vector3F w, final Vector3F v) {
		final Vector3F w0 = w.normalize();
		final Vector3F u0 = v.normalize().crossProduct(w0);
		final Vector3F v0 = w0.crossProduct(u0);
		
		return rotate(w0, v0, u0);
	}
	
	/**
	 * Returns a {@code Matrix44F} for rotating along the X-, Y- and Z-axes.
	 * <p>
	 * If either {@code w}, {@code v} or {@code u} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * u.x, u.y, u.z, 0
	 * v.x, v.y, v.z, 0
	 * w.x, w.y, w.z, 0
	 *   0,   0,   0, 1
	 * }
	 * </pre>
	 * 
	 * @param w a {@link Vector3F}
	 * @param v a {@code Vector3F}
	 * @param u a {@code Vector3F}
	 * @return a {@code Matrix44F} for rotating along the X-, Y- and Z-axes
	 * @throws NullPointerException thrown if, and only if, either {@code w}, {@code v} or {@code u} are {@code null}
	 */
	public static Matrix44F rotate(final Vector3F w, final Vector3F v, final Vector3F u) {
		return new Matrix44F(u.x, u.y, u.z, 0.0F, v.x, v.y, v.z, 0.0F, w.x, w.y, w.z, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
	/**
	 * Returns a {@code Matrix44F} for rotating along the X-axis.
	 * <p>
	 * If {@code angle} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * 1,    0,    0, 0
	 * 0, +cos, -sin, 0
	 * 0, +sin, +cos, 0
	 * 0,    0,    0, 1
	 * }
	 * </pre>
	 * 
	 * @param angle an {@link AngleF}
	 * @return a {@code Matrix44F} for rotating along the X-axis
	 * @throws NullPointerException thrown if, and only if, {@code angle} is {@code null}
	 */
	public static Matrix44F rotateX(final AngleF angle) {
		return new Matrix44F(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, cos(angle.radians), -sin(angle.radians), 0.0F, 0.0F, sin(angle.radians), cos(angle.radians), 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
	/**
	 * Returns a {@code Matrix44F} for rotating along the Y-axis.
	 * <p>
	 * If {@code angle} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * +cos, 0, +sin, 0
	 *    0, 1,    0, 0
	 * -sin, 0, +cos, 0
	 *    0, 0,    0, 1
	 * }
	 * </pre>
	 * 
	 * @param angle an {@link AngleF}
	 * @return a {@code Matrix44F} for rotating along the Y-axis
	 * @throws NullPointerException thrown if, and only if, {@code angle} is {@code null}
	 */
	public static Matrix44F rotateY(final AngleF angle) {
		return new Matrix44F(cos(angle.radians), 0.0F, sin(angle.radians), 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, -sin(angle.radians), 0.0F, cos(angle.radians), 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
	/**
	 * Returns a {@code Matrix44F} for rotating along the Z-axis.
	 * <p>
	 * If {@code angle} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * +cos, -sin, 0, 0
	 * +sin, +cos, 0, 0
	 *    0,    0, 1, 0
	 *    0,    0, 0, 1
	 * }
	 * </pre>
	 * 
	 * @param angle an {@link AngleF}
	 * @return a {@code Matrix44F} for rotating along the Z-axis
	 * @throws NullPointerException thrown if, and only if, {@code angle} is {@code null}
	 */
	public static Matrix44F rotateZ(final AngleF angle) {
		return new Matrix44F(cos(angle.radians), -sin(angle.radians), 0.0F, 0.0F, sin(angle.radians), cos(angle.radians), 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
	/**
	 * Returns a {@code Matrix44F} for scaling.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * x, 0, 0, 0
	 * 0, y, 0, 0
	 * 0, 0, z, 0
	 * 0, 0, 0, 1
	 * }
	 * </pre>
	 * 
	 * @param x the scale factor along the X-axis
	 * @param y the scale factor along the Y-axis
	 * @param z the scale factor along the Z-axis
	 * @return a {@code Matrix44F} for scaling
	 */
	public static Matrix44F scale(final float x, final float y, final float z) {
		return new Matrix44F(x, 0.0F, 0.0F, 0.0F, 0.0F, y, 0.0F, 0.0F, 0.0F, 0.0F, z, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
	/**
	 * Returns a {@code Matrix44F} for translation.
	 * <p>
	 * If {@code p} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * 1, 0, 0, p.x
	 * 0, 1, 0, p.y
	 * 0, 0, 1, p.z
	 * 0, 0, 0,   1
	 * }
	 * </pre>
	 * 
	 * @param p a {@link Point3F}
	 * @return a {@code Matrix44F} for translation
	 * @throws NullPointerException thrown if, and only if, {@code p} is {@code null}
	 */
	public static Matrix44F translate(final Point3F p) {
		return translate(p.x, p.y, p.z);
	}
	
	/**
	 * Returns a {@code Matrix44F} for translation.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * 1, 0, 0, x
	 * 0, 1, 0, y
	 * 0, 0, 1, z
	 * 0, 0, 0, 1
	 * }
	 * </pre>
	 * 
	 * @param x the translation factor along the X-axis
	 * @param y the translation factor along the Y-axis
	 * @param z the translation factor along the Z-axis
	 * @return a {@code Matrix44F} for translation
	 */
	public static Matrix44F translate(final float x, final float y, final float z) {
		return new Matrix44F(1.0F, 0.0F, 0.0F, x, 0.0F, 1.0F, 0.0F, y, 0.0F, 0.0F, 1.0F, z, 0.0F, 0.0F, 0.0F, 1.0F);
	}
}