`-*- mode: markdown; mode: visual-line; -*-`

# file-tree-tidier

Copy files from a source tree to a destination tree having a single level of directories named by the modification date of the files.

I wanted to pull all of the photos out of my messy iPhoto directory structure, with its odd menagerie of variously-named subdirectories, and into a simple directory containing a single level of subdirectories, named after the modification date of the files. Think of this as a poor man's `rsync`. It checks for files that have already been copied, and also checks for clashes (using MD5 checksums, which will make it rather slow).

## Usage

Feel free to create an *uberjar*, or else just run with Leiningen:

        lein run --from-dir ~/Pictures/iPhoto\ Library/Masters/ \
                 --to-dir ~/SOMEWHERE-MORE-SENSIBLE

Both root directories must exist.

The additional binary argument `--delete-after` will delete the source files (but not any of their containing directories) on copy (or if already copied).

## To Do

It's obviously possible to get clashes for identically-named files with the same modification date. At the moment this package will print the clashes and ignore them; we need to decide on a simple renaming scheme to apply instead.

## License

Copyright © 2014 Nick Rothwell.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
