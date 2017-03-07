# Prism [![Build Status](https://api.travis-ci.org/prism/Prism.png)](https://travis-ci.org/prism/Prism/)

*By viveleroi*

Prism is a rollback/restore grief-prevention plugin for Sponge-based Minecraft servers. 

![Prism](http://helion3.s3.amazonaws.com/prism.jpg)

Prism 3.x has been completely rewritten for Sponge. It's still very early in the development process but we're working hard to exceed even our own standards.

Because of this, our feature set is growing. File an issue or let us know in IRC what's important to you.

## Downloads

- [Builds](http://dhmc.us:8080/job/Prism/)

If using Mongo, download the `Prism-(version)-all.jar` file. Otherwise, download `Prism-(version).jar`.

- Prism 3.0.0-beta2+ supports SpongeAPI 4.0+
- Prism 3.0.0-beta1+ supports SpongeAPI 3.0+

## Installation

1. Place into your `mods` directory. 
2. Ensure your Prism configuration file has the proper database connection details.

## Features

- Unprecedented **block change tracking**. We've worked closely with the Sponge team to ensure it provides us the framework we need.
- Lookup & "near" commands.
- Rollback and Restore commands.
- "Undo" command.
- MongoDB Support (Recommended)
- H2, MySQL support.

## Commands

- `/pr l (parameters)` - Lookup records, filtering by parameters.
- `/pr near` - Query nearby records.
- `/pr rb (parameters)` - Rollback, filtering by parameters.
- `/pr rs (parameters)` - Restore, filtering by parameters.
- `/pr undo` - Undo your last rollback/restore action.

## Parameters

- `a:(event)` - "Action". `break`, `place`, etc.
- `b:(block id)` - "Block" name. Like "grass".
- `before:(time)` - "Before" time period.
- `c:(cause)` - Non-player causes, i.e. "environment"
- `player:(name)` - "Player" name. May be an offline player.
- `r:(number)` - "Radius" - A distance around you.
- `since:(time)` - "Since" time period.

## Flags

- `-clean` - Clean dangerous blocks and item drops from the rollback area.
- `-drain` - Drain liquids within a rollback area.
- `-no-group` - Prevent record grouping in lookups. Clicking on a single record will teleport you to those coordinates.

## Permissions

- `prism.info` - Can view information about Prism.
- `prism.help` - Can view help.
- `prism.inspect` - Can use inspection wand.
- `prism.lookup` - Can query records (via lookup or near).
- `prism.rollback` - Can perform a rollback.
- `prism.undo` - Can undo a rollback.
- `prism.override.radius` - Can exceed the maximum radius in the config.

## Configuration

Coming soon. Bug vive about this.

## Databases

Prism supports a variety of databases. We work hard to ensure is Prism is fast and efficient, but there are a lot of variables and no matter how much we tweak the "out-of-box" experience, the location, hardware, and configuration of your database servers can be a crucial factor.

###Mongo (Recommended)

[MongoDB](https://www.mongodb.com/) is a "no-sql" database, meaning it works very differently from sql-based databases. Rather than define fixed schemas, we store records as "documents". Given the variable nature of Minecraft and Mod data, it's difficult to work with a fixed schema.

We really appreciate other features like it's incredible performance. 

Mongo can be installed and run similarly to other database servers.

###MySQL/MariaDB

MySQL and the MariaDB fork are familiar products, especially for users of Prism 1/2. We continue to support these two products although native JSON support is essentially unavailable

Recent versions of MySQL support native JSON, while MariaDB has some support - although they've diverged in their handling. Supporting would require special work for each and is not a priority at this time.

###H2

H2 is a file-based storage engine which does not require any servers or setup on your part. It's useful only for testing or small servers. File-based storage engines are quite limited compared to database servers.

Unfortunately, H2 does not fully support batch inserts the way Prism needs -  this impacts Prism's performance as it either prevents us from using batches, or prevents us from utilizing table relationships.

Also, H2 doesn't seem to have a way of grouping by formatted data - in this case the date. It has to group on the column itself which would defeat the purpose. For now, records are grouped but without dates.

###Others?

Prism can be extended with support for additional storage engines. We always are open to PRs if you would like to submit further support.

## API

Prism 3 offers an API which allows you to:

- Query the database.
- Register custom action handlers (allow custom parameter parsing).
- Register custom result handlers (allow custom rollback/restores).
- ... and more!

Documentation Pending.

## IRC

Please follow prism development on `irc.esper.net` in `#prism`

## Credits

Prism 3 is the successor to Prism 1/2 (for Bukkit/Spigot/Cauldron). While much of the code has been rewritten it's still influenced by our original vision.

- viveleroi (*Creator, Lead Dev*)
- Dev Testing: Wolfire1, Ollie2000, ickyacky

#### Prism 1.x - 2.x

- bloodmc (*Assistance with MCPC+ special compatibility*)
- nasonfish (*Contributor*)
- YeaItsMe (*Release QA*)
- nasonfish, Natman93, YeaItsMe, mafoan (*Alpha Testers*)
- mafoan, randox24, tacovan, nehocbelac, Shampoo123, cxmmy14, Palczynski, drac17, ollie2000, PGKxNIGHTMARE, allies333, DocVanNostrand, drfizzman123, 00benallen, rachaelriott, PheonixTamer, YeaItsMe, Natman93, Brazter, sydney2005, rsleight, napalm1, Teh_Fishman, and plenty more from DHMC (*Live Testers on DHMC*)
- Artwork by [LegendarySoldier](http://legendary-soldier.deviantart.com/)


## YourKit

Thanks to YourKit for an open source license for their [java profiling application](http://www.yourkit.com/).

![YourKit](https://www.yourkit.com/images/yklogo.png)

## Donate to Vive

[![alt text][2]][1]

  [1]: https://www.paypal.com/cgi-bin/webscr?return=http%3A%2F%2Fdev.bukkit.org%2Fserver-mods%2Fprism%2F&cn=Add+special+instructions+to+the+addon+author%28s%29&business=botsko%40gmail.com&bn=PP-DonationsBF%3Abtn_donateCC_LG.gif%3ANonHosted&cancel_return=http%3A%2F%2Fdev.bukkit.org%2Fserver-mods%2Fprism%2F&lc=US&item_name=Prism+%28from+Bukkit.org%29&cmd=_donations&rm=1&no_shipping=1&currency_code=USD
  [2]: http://botsko.s3.amazonaws.com/paypal_donate.gif

Hey, I'm **viveleroi** and I'm responsible for 99% of Prism, the WebUI, the website, the documentation, responding to comments, IRC, and snowy tickets. But I also have a job and a family. Prism, and the rest of my plugins take an incalculable amount of time and that's hard to manage without any pay.

So please, *make a donation and make it easier for me to continue with these amazing plugins*.



### Compiling

Use the provided Gradle runtime to compile.

    ./gradlew build

### Development

- `git clone git@github.com:prism/Prism.git`
- `cp scripts/pre-commit .git/hooks`