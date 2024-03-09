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
		return (int64_t)uptr.release();
	}
	static void freeMemDirection( const int64_t p_dir ){
		mpreal* p = (mpreal*)p_dir;
		delete p;
		p = nullptr;
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