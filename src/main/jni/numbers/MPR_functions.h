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
};


//------------------------------------
#endif // _MPR_FUNCTIONS_H_