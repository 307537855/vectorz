package mikera.transformz;

import mikera.matrixx.DiagonalMatrix;
import mikera.matrixx.Matrixx;
import mikera.transforz.impl.IdentityTranslation;
import mikera.vectorz.AVector;

public class Transformz {
	public static DiagonalMatrix scale3D(double factor) {
		return Matrixx.createScaleMatrix(3, factor);
	}
	
	public static IdentityTranslation identityTransform(int dimensions) {
		return IdentityTranslation.getInstance(dimensions);
	}
	
	public static PureTranslation createTranslation(AVector v) {
		return new PureTranslation(v);
	}
}
