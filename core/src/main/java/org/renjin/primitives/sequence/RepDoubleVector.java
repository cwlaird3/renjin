package org.renjin.primitives.sequence;


import org.renjin.primitives.vector.DeferredComputation;
import org.renjin.sexp.AttributeMap;
import org.renjin.sexp.DoubleVector;
import org.renjin.sexp.IntArrayVector;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.Vector;

public class RepDoubleVector extends DoubleVector implements DeferredComputation {

  public static final int LENGTH_THRESHOLD = 100;

  private final Vector source;
  private int length;
  private int each;

  public RepDoubleVector(Vector source, int length, int each, AttributeMap attributes) {
    super(attributes);
    this.source = source;
    this.length = length;
    this.each = each;
    if(this.length <= 0) {
      throw new IllegalArgumentException("length: " + length);
    }
  }

  public RepDoubleVector(Vector source, int length, int each) {
    this(source, length, each, transformAttributes(source, length, each));
  }
  
  private RepDoubleVector(double constant, int length) {
    super(AttributeMap.EMPTY);
    this.source = DoubleVector.valueOf(constant);
    this.length = length;
    this.each = 1;
  }

  private static AttributeMap transformAttributes(Vector source, int length, int each) {
    if(source.getAttributes().hasNames()) {
      return source.getAttributes()
              .copy()
              .setNames(new RepStringVector(source.getAttributes().getNames(), length, each, AttributeMap.EMPTY))
              .build();
    } else {
      return source.getAttributes();
    }
  }

  public static DoubleVector createConstantVector(double constant,
      int length) {
    if (length <= 0) {
      return DoubleVector.EMPTY;
    }
    return new RepDoubleVector(constant, length);
  }

  @Override
  protected SEXP cloneWithNewAttributes(AttributeMap attributes) {
    return new RepDoubleVector(source, length, each, attributes);
  }

  @Override
  public double getElementAsDouble(int index) {
    return source.getElementAsDouble((index / each) % source.length());
  }

  @Override
  public boolean isConstantAccessTime() {
    return true;
  }

  @Override
  public int length() {
    return length;
  }

  @Override
  public Vector[] getOperands() {
    return new Vector[] { source, new IntArrayVector(length/each/source.length()), new IntArrayVector(each) };
  }

  @Override
  public String getComputationName() {
    return "rep";
  }
}
