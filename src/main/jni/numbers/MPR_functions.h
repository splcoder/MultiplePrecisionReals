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
	static int64_t operation2( const int64_t lPtr, const double r, int ope ){
		int64_t res = 0;
		mpreal *left = (mpreal*)lPtr, right = r;
		MPRF::exeOperation( res, ope, *left, right );
		left = nullptr;
		return res;
	}
};


//------------------------------------
#endif // _MPR_FUNCTIONS_H_