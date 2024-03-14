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
	#include <mutex>
	std::map<int64_t, std::unique_ptr<mpreal>> map_for_mpr;
	std::mutex map_for_mpr_mutex;
#endif
//----------------------------------------------------------------------------------------------------------------------

// MPR mem Functions
class MPRF {
private:
	MPRF(){}

public:
	static int64_t getMemDirection( const mpreal &value ){
		std::unique_ptr<mpreal> uptr = std::make_unique<mpreal>( std::move( value ) );
		#ifdef USE_HELMET_FOR_MPR
			int64_t n_ptr = (int64_t)uptr.get();
			std::lock_guard<std::mutex> guard( map_for_mpr_mutex );
			map_for_mpr[ n_ptr ] = std::move( uptr );
			return n_ptr;
		#else
			return (int64_t)uptr.release();
		#endif
	}
	static void freeMemDirection( const int64_t p_dir ){
		#ifdef USE_HELMET_FOR_MPR
			std::lock_guard<std::mutex> guard( map_for_mpr_mutex );
			map_for_mpr.erase( p_dir );
    	#else
    		mpreal* p = (mpreal*)p_dir;
            delete p;
            p = nullptr;
    	#endif
	}
};


//------------------------------------
#endif // _MPR_FUNCTIONS_H_