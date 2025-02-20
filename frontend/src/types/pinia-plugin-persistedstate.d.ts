import 'pinia-plugin-persistedstate'

declare module 'pinia' {
  export interface DefineStoreOptionsBase<S, Store> {
    persist?: boolean | {
      paths?: string[]
      storage?: Storage
      key?: string
      beforeRestore?: (context: { store: Store }) => void
      afterRestore?: (context: { store: Store }) => void
      serializer?: {
        serialize: (value: any) => string
        deserialize: (value: string) => any
      }
    }
  }
} 