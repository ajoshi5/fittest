module Paths_lopi (
    version,
    getBinDir, getLibDir, getDataDir, getLibexecDir,
    getDataFileName
  ) where

import qualified Control.Exception as Exception
import Data.Version (Version(..))
import System.Environment (getEnv)
import Prelude

catchIO :: IO a -> (Exception.IOException -> IO a) -> IO a
catchIO = Exception.catch


version :: Version
version = Version {versionBranch = [1,0], versionTags = []}
bindir, libdir, datadir, libexecdir :: FilePath

bindir     = "/Users/urueda/Library/Haskell/ghc-7.6.3/lib/lopi-1.0/bin"
libdir     = "/Users/urueda/Library/Haskell/ghc-7.6.3/lib/lopi-1.0/lib"
datadir    = "/Users/urueda/Library/Haskell/ghc-7.6.3/lib/lopi-1.0/share"
libexecdir = "/Users/urueda/Library/Haskell/ghc-7.6.3/lib/lopi-1.0/libexec"

getBinDir, getLibDir, getDataDir, getLibexecDir :: IO FilePath
getBinDir = catchIO (getEnv "lopi_bindir") (\_ -> return bindir)
getLibDir = catchIO (getEnv "lopi_libdir") (\_ -> return libdir)
getDataDir = catchIO (getEnv "lopi_datadir") (\_ -> return datadir)
getLibexecDir = catchIO (getEnv "lopi_libexecdir") (\_ -> return libexecdir)

getDataFileName :: FilePath -> IO FilePath
getDataFileName name = do
  dir <- getDataDir
  return (dir ++ "/" ++ name)
