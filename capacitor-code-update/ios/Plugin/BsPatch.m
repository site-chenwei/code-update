//
//  BsPatch.m
//  Plugin
//
//  Created by 陈炜 on 2022/3/31.
//  Copyright © 2022 Max Lynch. All rights reserved.
//

#import "BsPatch.h"
@implementation BsPatch

+(void)patch:(NSString*)oldPath newPath:(NSString*)newPath patchPath:(NSString*)patchPath{
    const char *argv[4];
        argv[0] = "bspatch";
        // oldPath
        argv[1] = [oldPath UTF8String];
        // newPath
        argv[2] = [newPath UTF8String];
        // patchPath
        argv[3] = [patchPath UTF8String];
        BsdiffUntils_bspatch(4,argv);
}

@end
