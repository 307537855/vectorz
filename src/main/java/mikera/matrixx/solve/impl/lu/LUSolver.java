/*
 * Copyright (c) 2009-2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mikera.matrixx.solve.impl.lu;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.impl.lu.AltLU;
import mikera.matrixx.decompose.impl.lu.LUPResult;
import mikera.matrixx.impl.ADenseArrayMatrix;

/**
 * @author Peter Abeles
 */
public class LUSolver {

    protected AltLU decomp;
    private LUPResult result;
    boolean doImprove = false;
    protected AMatrix A;
    protected int numRows;
    protected int numCols;

    public AMatrix getA() {
        return A;
    }

    public LUSolver( boolean improve ) {
        this.doImprove = improve;
    }
    public LUSolver() {
        this.doImprove = false;
    }

    public LUPResult setA(AMatrix A) {
        if(!A.isSquare())
            throw new IllegalArgumentException("Input must be a square matrix.");
        this.A = A;
        this.numRows = A.rowCount();
        this.numCols = A.columnCount();
        decomp = new AltLU();
        result = decomp._decompose(A);
        return result;
    }

    public double quality() {
        return decomp.quality();
    }

    public AMatrix invert() {
        if (!A.isSquare()) { throw new IllegalArgumentException(
            "Matrix must be square for inverse!"); }

        double []vv = decomp._getVV();
        AMatrix LU = decomp.getLU();
        
        Matrix A_inv = Matrix.create(LU.rowCount(), LU.columnCount());

        int n = A.columnCount();

        double dataInv[] = A_inv.data;

        for( int j = 0; j < n; j++ ) {
            // don't need to change inv into an identity matrix before hand
            for( int i = 0; i < n; i++ ) vv[i] = i == j ? 1 : 0;
            decomp._solveVectorInternal(vv);
//            for( int i = 0; i < n; i++ ) dataInv[i* n +j] = vv[i];
            int index = j;
            for( int i = 0; i < n; i++ , index += n) dataInv[ index ] = vv[i];
        }

        return A_inv;
    }
    
    public ADenseArrayMatrix solve(AMatrix b) {
        if( b.rowCount() != numCols )
            throw new IllegalArgumentException("Unexpected matrix size");
        if(Math.abs(result.computeDeterminant()) < 1e-10)
            return null;
            
        Matrix x = Matrix.create(numCols, b.columnCount());

        int numCols = b.columnCount();

        double dataB[] = b.asDoubleArray();
        if (dataB == null) {
            dataB = b.toDoubleArray();
        }
        double dataX[] = x.data;

        double []vv = decomp._getVV();

//        for( int j = 0; j < numCols; j++ ) {
//            for( int i = 0; i < this.numCols; i++ ) vv[i] = dataB[i*numCols+j];
//            decomp._solveVectorInternal(vv);
//            for( int i = 0; i < this.numCols; i++ ) dataX[i*numCols+j] = vv[i];
//        }
        for( int j = 0; j < numCols; j++ ) {
            int index = j;
            for( int i = 0; i < this.numCols; i++ , index += numCols ) vv[i] = dataB[index];
            decomp._solveVectorInternal(vv);
            index = j;
            for( int i = 0; i < this.numCols; i++ , index += numCols ) dataX[index] = vv[i];
        }

        if( doImprove ) {
            improveSol(b,x);
        }
        return x;
    }

    /**
     * This attempts to improve upon the solution generated by account
     * for numerical imprecisions.  See numerical recipes for more information.  It
     * is assumed that solve has already been run on 'b' and 'x' at least once.
     *
     * @param b A matrix. Not modified.
     * @param x A matrix. Modified.
     */
    public void improveSol( AMatrix b , AMatrix x )
    {
        if( b.columnCount() != x.columnCount() ) {
            throw new IllegalArgumentException("bad shapes");
        }

        double dataA[] = A.asDoubleArray();
        double dataB[] = b.asDoubleArray();
        double dataX[] = x.asDoubleArray();

        final int nc = b.columnCount();
        final int n = b.columnCount();

        double []vv = decomp._getVV();
//        AMatrix LU = decomp.getLU();

//        BigDecimal sdp = new BigDecimal(0);
        for( int k = 0; k < nc; k++ ) {
            for( int i = 0; i < n; i++ ) {
                // *NOTE* in the book this is a long double.  extra precision might be required
                double sdp = -dataB[ i * nc + k];
//                BigDecimal sdp = new BigDecimal(-dataB[ i * nc + k]);
                for( int j = 0; j < n; j++ ) {
                    sdp += dataA[i* n +j] * dataX[ j * nc + k];
//                    sdp = sdp.add( BigDecimal.valueOf(dataA[i* n +j] * dataX[ j * nc + k]));
                }
                vv[i] = sdp;
//                vv[i] = sdp.doubleValue();
            }
            decomp._solveVectorInternal(vv);
            for( int i = 0; i < n; i++ ) {
                dataX[i*nc + k] -= vv[i];
            }
        }
    }
}