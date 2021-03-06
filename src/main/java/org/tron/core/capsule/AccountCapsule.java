/*
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.tron.core.capsule;

import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.common.utils.ByteArray;
import org.tron.protos.Contract.AccountCreateContract;
import org.tron.protos.Protocol.Account;
import org.tron.protos.Protocol.Account.Vote;
import org.tron.protos.Protocol.AccountType;

public class AccountCapsule implements ProtoCapsule<Account> {

  protected static final Logger logger = LoggerFactory.getLogger("AccountCapsule");

  private Account account;

  /**
   * get account from bytes data.
   */
  public AccountCapsule(byte[] data) {
    try {
      this.account = Account.parseFrom(data);
    } catch (InvalidProtocolBufferException e) {
      logger.debug(e.getMessage());
    }
  }

  /**
   * initial account capsule.
   */
  public AccountCapsule(ByteString accountName, AccountType accountType, ByteString address,
      long balance) {
    this.account = Account.newBuilder()
        .setAccountName(accountName)
        .setType(accountType)
        .setAddress(address)
        .setBalance(balance)
        .build();
  }

  /**
   * get account from contract.
   */
  public AccountCapsule(final AccountCreateContract contract) {
    this.account = Account.newBuilder()
        .setAccountName(contract.getAccountName())
        .setType(contract.getType())
        .setAddress(contract.getOwnerAddress())
        .setTypeValue(contract.getTypeValue())
        .build();
  }

  /**
   * get account from address and account name.
   */
  public AccountCapsule(ByteString address, ByteString accountName,
      AccountType accountType) {
    this.account = Account.newBuilder()
        .setType(accountType)
        .setAddress(address)
        .build();
  }

  public AccountCapsule(Account account) {
    this.account = account;
  }

  public byte[] getData() {
    return this.account.toByteArray();
  }

  @Override
  public Account getInstance() {
    return this.account;
  }

  public ByteString getAddress() {
    return this.account.getAddress();
  }

  public AccountType getType() {
    return this.account.getType();
  }


  public long getBalance() {
    return this.account.getBalance();
  }

  public long getLatestOperationTime() {
    return this.account.getLatestOprationTime();
  }

  public void setBalance(long balance) {
    this.account = this.account.toBuilder().setBalance(balance).build();
  }

  @Override
  public String toString() {
    return this.account.toString();
  }


  /**
   * set votes.
   */
  public void addVotes(ByteString voteAddress, long voteAdd) {
    this.account = this.account.toBuilder()
        .addVotes(Vote.newBuilder().setVoteAddress(voteAddress).setVoteCount(voteAdd).build())
        .build();
  }

  /**
   * get votes.
   */
  public List<Vote> getVotesList() {
    if (this.account.getVotesList() != null) {
      return this.account.getVotesList();
    } else {
      return Lists.newArrayList();
    }
  }

  public long getShare() {
    return this.account.getBalance();
  }

  public Map<String, Long> getAsset() {
    return this.account.getAssetMap();
  }

  /**
   * reduce asset amount.
   */
  public boolean reduceAssetAmount(ByteString name, long amount) {
    Map<String, Long> assetMap = this.account.getAssetMap();

    String nameKey = ByteArray.toHexString(name.toByteArray());

    Long currentAmount = assetMap.get(nameKey);

    if (amount > 0 && amount <= currentAmount) {
      this.account = this.account.toBuilder().putAsset(nameKey, currentAmount - amount).build();
      return true;
    }

    return false;
  }

  /**
   * add asset amount.
   */
  public boolean addAssetAmount(ByteString name, long amount) {
    Map<String, Long> assetMap = this.account.getAssetMap();

    String nameKey = ByteArray.toHexString(name.toByteArray());

    Long currentAmount = assetMap.get(nameKey);

    if (currentAmount == null) {
      currentAmount = 0L;
    }

    this.account = this.account.toBuilder().putAsset(nameKey, currentAmount + amount).build();

    return true;
  }
}
