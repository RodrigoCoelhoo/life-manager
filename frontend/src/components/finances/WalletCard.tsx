import { useState } from "react";
import { Modal } from "../common/Modal";
import { LuWallet } from "react-icons/lu";
import { HiOutlineCash } from "react-icons/hi";
import type { WalletResponseDTO, WalletUpdateDTO } from "../../services/finances/wallet/wallet.dto";
import WalletForm from "./WalletForm";
import { formatBalance } from "../../services/finances/currencies.type";

interface WalletCardProps extends WalletResponseDTO {
	onUpdate: (id: number, data: WalletUpdateDTO) => Promise<void>;
	onDelete: (id: number) => Promise<void>;
}

export default function WalletCard({ onUpdate, onDelete, ...wallet }: WalletCardProps) {
	const [isOpen, setIsOpen] = useState(false);

	return (
		<>
			<div
				className="p-4 bg-foreground h-60 rounded-lg flex flex-col shadow-md cursor-pointer hover:shadow-xl transition hover:scale-[1.02] hover:border hover:border-secondary text-textcolor"
				onClick={() => setIsOpen(true)}
			>
				<div className="flex flex-row justify-between items-center p-2">
					{
						wallet.type === "BANK" ? (
							<LuWallet size={42} className="bg-primary p-2 rounded-lg" />
						) : (
							<HiOutlineCash size={42} className="bg-secondary p-2 rounded-lg" />
						)
					}

					<span className="bg-secondary/25 rounded-xl px-2 py-1 font-extralight text-textcolor/80">{wallet.type}</span>
				</div>

				<div className="p-2 flex flex-col justify-between h-full">
					<div>
						<h2 className="text-xl">{wallet.name}</h2>
						<span className="font-extralight text-textcolor/80">
							{wallet.currency} Account
						</span>
					</div>

					<div className="border border-primary/40">

					</div>

					<div>
						<span className="text-textcolor/80">
							Balance
						</span>

						<div className="text-xl">
							{formatBalance(wallet.balance)}
						</div>
					</div>
				</div>

			</div>

			<Modal isOpen={isOpen} onClose={() => setIsOpen(false)}>
				<WalletForm
					wallet={wallet}
					onClose={() => setIsOpen(false)}
					onUpdate={onUpdate}
					onDelete={onDelete}
				/>
			</Modal>
		</>
	);
}
