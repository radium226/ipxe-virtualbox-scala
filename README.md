# Adding `Bar` in `Foo` Functionalities
1. Create the `com.github.radium226.foo` package
2. Create the `com/github/radium226/foo/bar.scala` file
3. Define the `Baz` domain case class
4. Define the `BarConfig` config case class
5. Add the `BarModule[F[_]]` trait and the `BarModule` companion object
6. Add the `BarInstances` trait and the `implicit def barInstance[F[_]](implicit A: ApplicativeAsk[F, BarConfig]): BarModule[F]` method
7. In the `com.github.radium226.InstancesForConfig` trait, add the `implicit def barInstanceForConfig[F[_]](implicit barInstance: BarModule[ReaderT[F, BarConfig, *]]): BarModule[ReaderT[F, Config, *]] = new BarModule[F] { ... }` and use `ReaderT`'s `.local` to convert `Config` to `BarConfig`