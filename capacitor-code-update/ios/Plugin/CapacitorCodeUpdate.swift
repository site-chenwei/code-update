import Foundation

@objc public class CapacitorCodeUpdate: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
