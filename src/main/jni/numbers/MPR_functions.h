#ifndef _MPR_FUNCTIONS_H_
#define _MPR_FUNCTIONS_H_
//------------------------------------

#include <iostream>
#include <memory>
#include "./mpreal.h"

using namespace std;
using mpfr::mpreal;

/*
COMPILER:
	1st libmpfr.a
	2nd libgmpxx.a
	3rd libgmp.a
*/

//----------------------------------------------------------------------------------------------------------------------
// You can increase the speed by commenting the next line, but it might be crazy
# define USE_HELMET_FOR_MPR

#ifdef USE_HELMET_FOR_MPR
	#include <map>
	std::map<int64_t, std::unique_ptr<mpreal>> map_for_mpr;
#endif
//----------------------------------------------------------------------------------------------------------------------

// MPR Functions
class MPRF {
private:
	MPRF(){}

	static void exeOperation( int64_t &out, int ope, const mpreal &left, const mpreal &right ){
		switch( ope ){
			case 0:		out = getMemDirection( left + right );					break;
			case 1:		out = getMemDirection( left - right );					break;
			case 2:		out = getMemDirection( left * right );					break;
			case 3:		out = getMemDirection( left / right );					break;
			case 4:		out = getMemDirection( hypot( left, right ) );			break;
			case 5:		out = getMemDirection( pow( left, right ) );			break;
			case 6:		out = getMemDirection( log( left ) / log( right ) );	break;
			case 7:		out = getMemDirection( atan2( left, right ) );			break;
			case 8:		out = getMemDirection( fmax( left, right ) );			break;
			case 9:		out = getMemDirection( fmin( left, right ) );			break;
			case 10:	out = getMemDirection( fmod( left, right ) );			break;
			case 11:	out = getMemDirection( rem( left, right ) );			break;
			case 12:	out = getMemDirection( mod( left, right ) );			break;
			default:	out = 0;	// nullptr (it never reachs this point)
		}
	}
	static void exeOperationForOne( int64_t &out, int ope, const mpreal &value ){
    		switch( ope ){
    			case 0:		out = getMemDirection( -value );				break;
    			case 1:		out = getMemDirection( sqr( value ) );			break;
    			case 2:		out = getMemDirection( fabs( value ) );			break;
    			case 3:		out = getMemDirection( floor( value ) );		break;
    			case 4:		out = getMemDirection( ceil( value ) );			break;
    			case 5:		out = getMemDirection( trunc( value ) );		break;
    			case 6:		out = getMemDirection( round( value ) );		break;
    			case 7:		out = getMemDirection( sqrt( value ) );			break;
    			case 8:		out = getMemDirection( cbrt( value ) );			break;
    			case 9:		out = getMemDirection( exp( value ) );			break;
    			case 10:	out = getMemDirection( exp2( value ) );			break;
    			case 11:	out = getMemDirection( exp10( value ) );		break;
    			case 12:	out = getMemDirection( expm1( value ) );		break;
    			case 13:	out = getMemDirection( exp( value )*value );	break;
    			case 14:	out = getMemDirection( log( value ) );			break;
    			case 15:	out = getMemDirection( log1p( value ) );		break;
    			case 16:	out = getMemDirection( log2( value ) );			break;
    			case 17:	out = getMemDirection( log10( value ) );		break;
    			case 18:	out = getMemDirection( sin( value ) );			break;
    			case 19:	out = getMemDirection( cos( value ) );			break;
    			case 20:	out = getMemDirection( tan( value ) );			break;
    			case 21:	out = getMemDirection( csc( value ) );			break;
    			case 22:	out = getMemDirection( sec( value ) );			break;
    			case 23:	out = getMemDirection( cot( value ) );			break;
    			case 24:	out = getMemDirection( asin( value ) );			break;
    			case 25:	out = getMemDirection( acos( value ) );			break;
    			case 26:	out = getMemDirection( atan( value ) );			break;
    			case 27:	out = getMemDirection( acsc( value ) );			break;
				case 28:	out = getMemDirection( asec( value ) );			break;
				case 29:	out = getMemDirection( acot( value ) );			break;
				case 30:	out = getMemDirection( sinh( value ) );			break;
				case 31:	out = getMemDirection( cosh( value ) );			break;
				case 32:	out = getMemDirection( tanh( value ) );			break;
				case 33:	out = getMemDirection( csch( value ) );			break;
				case 34:	out = getMemDirection( sech( value ) );			break;
				case 35:	out = getMemDirection( coth( value ) );			break;
				case 36:	out = getMemDirection( asinh( value ) );		break;
				case 37:	out = getMemDirection( acosh( value ) );		break;
				case 38:	out = getMemDirection( atanh( value ) );		break;
				case 39:	out = getMemDirection( acsch( value ) );		break;
				case 40:	out = getMemDirection( asech( value ) );		break;
				case 41:	out = getMemDirection( acoth( value ) );		break;
				case 42:	out = getMemDirection( besselj0( value ) );		break;
				case 43:	out = getMemDirection( besselj1( value ) );		break;
    			default: out = 0;	// nullptr (it never reachs this point)
    		}
    	}

public:
	static int64_t getMemDirection( const mpreal &value ){
		std::unique_ptr<mpreal> uptr = std::make_unique<mpreal>( std::move( value ) );
		#ifdef USE_HELMET_FOR_MPR
			int64_t n_ptr = (int64_t)uptr.get();
			map_for_mpr[ n_ptr ] = std::move( uptr );
			return n_ptr;
		#else
			return (int64_t)uptr.release();
		#endif
	}
	static void freeMemDirection( const int64_t p_dir ){
		#ifdef USE_HELMET_FOR_MPR
			map_for_mpr.erase( p_dir );
    	#else
    		mpreal* p = (mpreal*)p_dir;
            delete p;
            p = nullptr;
    	#endif
	}
	static int64_t operation( const int64_t lPtr, const int64_t rPtr, int ope ){
		int64_t res = 0;
		mpreal *left = (mpreal*)lPtr, *right = (mpreal*)rPtr;
		MPRF::exeOperation( res, ope, *left, *right );
		left = nullptr; right = nullptr;
		return res;
	}
	static int64_t operation2( const int64_t lPtr, const double ri, int ope ){
		int64_t res = 0;
		mpreal *left = (mpreal*)lPtr, right( ri );
		MPRF::exeOperation( res, ope, *left, right );
		left = nullptr;
		return res;
	}
	static int64_t operation3( const double le, const int64_t rPtr, int ope ){
		int64_t res = 0;
		mpreal left( le ), *right = (mpreal*)rPtr;
		MPRF::exeOperation( res, ope, left, *right );
		right = nullptr;
		return res;
	}
	static int64_t operation4( const double le, const double ri, int ope ){
		int64_t res = 0;
		mpreal left( le ), right( ri );
		MPRF::exeOperation( res, ope, left, right );
		return res;
	}
	static int64_t operation5( const int64_t ptr, int ope ){
		int64_t res = 0;
		mpreal *value = (mpreal*)ptr;
		MPRF::exeOperationForOne( res, ope, *value );
		value = nullptr;
		return res;
	}
	static int64_t operation6( const double value, int ope ){
		int64_t res = 0;
		MPRF::exeOperationForOne( res, ope, (mpreal)value );
		return res;
	}
};


//------------------------------------
#endif // _MPR_FUNCTIONS_H_