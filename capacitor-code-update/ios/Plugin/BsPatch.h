//
//  BsPatch.h
//  Plugin
//
//  Created by 陈炜 on 2022/3/31.
//  Copyright © 2022 Max Lynch. All rights reserved.
//

#ifndef BsPatch_h
#define BsPatch_h

#import <Foundation/Foundation.h>
#import "Test_Bsdiff.h"
@interface BsPatch : NSObject
+(void)patch:(NSString*)oldPath newPath:(NSString*)newPath patchPath:(NSString*)patchPath;
@end

#endif /* BsPatch_h */

