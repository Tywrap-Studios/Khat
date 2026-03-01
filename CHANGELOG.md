### 2.0.1 Transitive Dependencies Hotfix
Fixed major bugs, particularly present in the `krpc` and `full` versions, related
to transitive dependencies not being present in the `include` configurations.
This lead to all the functionality not working because the needed classes were
not present during runtime.