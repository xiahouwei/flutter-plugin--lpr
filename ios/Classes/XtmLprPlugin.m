#import "XtmLprPlugin.h"
#if __has_include(<xtm_lpr/xtm_lpr-Swift.h>)
#import <xtm_lpr/xtm_lpr-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "xtm_lpr-Swift.h"
#endif

@implementation XtmLprPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftXtmLprPlugin registerWithRegistrar:registrar];
}
@end
