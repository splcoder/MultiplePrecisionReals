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
			case 0: out = getMemDirection( left + right ); break;
			case 1: out = getMemDirection( left - right ); break;
			case 2: out = getMemDirection( left * right ); break;
			case 3: out = getMemDirection( left / right ); break;
			case 4: out = getMemDirection( hypot( left, right ) ); break;
			case 5: out = getMemDirection( pow( left, right ) ); break;
			default: out = 0;	// nullptr (it never reachs this point)
		}
	}
	static void exeOperationForOne( int64_t &out, int ope, const mpreal &value ){
    		switch( ope ){
    			case 0: out = getMemDirection( -value ); break;
    			case 1: out = getMemDirection( sqr( value ) ); break;
    			case 2: out = getMemDirection( fabs( value ) ); break;
    			case 3: out = getMemDirection( floor( value ) ); break;
    			case 4: out = getMemDirection( ceil( value ) ); break;
    			case 5: out = getMemDirection( trunc( value ) ); break;
    			case 6: out = getMemDirection( round( value ) ); break;
    			case 7: out = getMemDirection( sqrt( value ) ); break;
    			case 8: out = getMemDirection( cbrt( value ) ); break;
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
		mpreal *left = (mpreal*)lPtr, right = ri;
		MPRF::exeOperation( res, ope, *left, right );
		left = nullptr;
		return res;
	}
	static int64_t operation1( const int64_t ptr, int ope ){
		int64_t res = 0;
		mpreal *value = (mpreal*)ptr;
		MPRF::exeOperationForOne( res, ope, *value );
		value = nullptr;
		return res;
	}
	static int64_t operation1Double( const double value, int ope ){
		int64_t res = 0;
		MPRF::exeOperationForOne( res, ope, (mpreal)value );
		return res;
	}
	static int64_t operation3( const double le, const int64_t rPtr, int ope ){
		int64_t res = 0;
		mpreal left = le, *right = (mpreal*)rPtr;
		MPRF::exeOperation( res, ope, left, *right );
		right = nullptr;
		return res;
	}
	static int64_t operation4( const double le, const double ri, int ope ){
		int64_t res = 0;
		mpreal left = le, right = ri;
		MPRF::exeOperation( res, ope, left, right );
		return res;
	}
};


//------------------------------------
#endif // _MPR_FUNCTIONS_H_