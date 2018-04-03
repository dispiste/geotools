/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.measure;

import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.format.UnitFormat;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import si.uom.NonSI;
import si.uom.SI;
import systems.uom.common.USCustomary;
import tec.uom.se.AbstractUnit;
import tec.uom.se.format.SimpleUnitFormat;
import tec.uom.se.function.MultiplyConverter;
import tec.uom.se.unit.TransformedUnit;


/**
 * A set of units to use in addition of {@link SI} and {@link NonSI}.
 *
 * @since 2.1
 *
 *
 * @source $URL$
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public final class Units {
    /**
     * Do not allows instantiation of this class.
     */
    private Units() {
    }
    /**
     * Length of <code>1/72</code> of a {@link USCustomary#INCH}
     */
    public static final Unit<Length> PIXEL = USCustomary.INCH.divide(72);
    
    /**
     * Time duration of <code>1/12</code> of a {@link SI#YEAR}.
     */
    public static final Unit<Time> MONTH = SI.YEAR.divide(12);
    
    /**
     * Pseudo-unit for sexagesimal degree. Numbers in this pseudo-unit has the following format:
     *
     * <cite>sign - degrees - decimal point - minutes (two digits) - integer seconds (two digits) -
     * fraction of seconds (any precision)</cite>.
     *
     * This unit is non-linear and not pratical for computation. Consequently, it should be
     * avoid as much as possible. Unfortunatly, this pseudo-unit is extensively used in the
     * EPSG database (code 9110).
     */
    public static final Unit<Angle> SEXAGESIMAL_DMS = NonSI.DEGREE_ANGLE.transform(
            SexagesimalConverter.FRACTIONAL.inverse()).asType(Angle.class);

    /**
     * Pseudo-unit for degree - minute - second. Numbers in this pseudo-unit has the following
     * format:
     *
     * <cite>signed degrees (integer) - arc-minutes (integer) - arc-seconds
     * (real, any precision)</cite>.
     *
     * This unit is non-linear and not pratical for computation. Consequently, it should be
     * avoid as much as possible. Unfortunatly, this pseudo-unit is extensively used in the
     * EPSG database (code 9107).
     */
    public static final Unit<Angle> DEGREE_MINUTE_SECOND = NonSI.DEGREE_ANGLE.transform(
            SexagesimalConverter.INTEGER.inverse()).asType(Angle.class);

    /**
     * Parts per million.
     */
    public static final Unit<Dimensionless> PPM = AbstractUnit.ONE.multiply(1E-6);

    /**
     * Associates the labels to units created in this class.
     */
    static {
        final UnitFormat format = SimpleUnitFormat.getInstance();
        registerCustomUnits((SimpleUnitFormat) format);
    }

    /**
     * Registers the labels and aliases for the custom units defined by Geotools.
     * 
     * @param format The UnitFormat in which the labels and aliases must be registered.
     */
    public static void registerCustomUnits(SimpleUnitFormat format) {
        format.label(Units.DEGREE_MINUTE_SECOND, "DMS");
        format.alias(Units.DEGREE_MINUTE_SECOND, "degree minute second");

        format.label(Units.SEXAGESIMAL_DMS, "D.MS");
        format.alias(Units.SEXAGESIMAL_DMS, "sexagesimal DMS");
        format.alias(Units.SEXAGESIMAL_DMS, "DDD.MMSSsss");
        format.alias(Units.SEXAGESIMAL_DMS, "sexagesimal degree DDD.MMSSsss");

        format.label(Units.PPM, "ppm");
    }

    /**
     * Recognize representation of NonSI.DEEGREE_ANGLE to prevent unnecessary conversion.
     * 
     * @param unit
     * @return true if MultiplyConverter is close to PI/180.0
     */
    public static final boolean isDegreeAngle(Unit<?> unit) {
        if (unit.getSystemUnit() == SI.RADIAN && unit instanceof TransformedUnit<?>) {
            @SuppressWarnings("unchecked")
            TransformedUnit<Angle> transformed = (TransformedUnit<Angle>) unit;
            UnitConverter converter = transformed.getConverter();
            if (converter instanceof MultiplyConverter) {
                MultiplyConverter multiplyConverter = (MultiplyConverter) converter;
                double factor = multiplyConverter.getFactor();
                if (factor == 0.017453292519943295 || factor == 0.0174532925199433
                        || factor == 0.01745329251994328) {
                    return true;
                }
            }
        }
        return false;
    }
}
